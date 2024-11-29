package com.android.greenmate.utils

import android.content.Context
import android.graphics.Bitmap
import android.icu.text.DecimalFormat
import com.android.greenmate.data.model.ImageInferenceResult
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.common.FileUtil
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp

object TensorFlowLiteUtils {
    fun preprocess(bitmap: Bitmap): ByteBuffer {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val resizedBitmap = Bitmap.createScaledBitmap(mutableBitmap, 288, 288, false)
        val croppedBitmap = Bitmap.createBitmap(
            resizedBitmap,
            (resizedBitmap.width - 256) / 2,
            (resizedBitmap.height - 256) / 2,
            256,
            256
        )

        val byteBuffer = ByteBuffer.allocateDirect(4 * 1 * 3 * 256 * 256)
//        val byteBuffer = ByteBuffer.allocateDirect(4 * 1 * 3 * 224 * 224)
        byteBuffer.order(ByteOrder.nativeOrder())

//        val intValues = IntArray(224 * 224)
//        croppedBitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)
        val intValues = IntArray(256 * 256)
        croppedBitmap.getPixels(intValues, 0, 256, 0, 0, 256, 256)

        val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val std = floatArrayOf(0.229f, 0.224f, 0.225f)

        // Add the batch dimension in NCHW format
        for (c in 0 until 3) { // For each channel
            for (y in 0 until 256) {
                for (x in 0 until 256) {
                    val pixel = intValues[y * 256 + x]

                    val r = (pixel shr 16 and 0xFF) / 255.0f
                    val g = (pixel shr 8 and 0xFF) / 255.0f
                    val b = (pixel and 0xFF) / 255.0f

                    when (c) {
                        0 -> byteBuffer.putFloat((r - mean[c]) / std[c]) // R channel
                        1 -> byteBuffer.putFloat((g - mean[c]) / std[c]) // G channel
                        2 -> byteBuffer.putFloat((b - mean[c]) / std[c]) // B channel
                    }
                }
            }
        }

        return byteBuffer
    }

    fun diseasePreprocess(bitmap: Bitmap): ByteBuffer {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val resizedBitmap = Bitmap.createScaledBitmap(mutableBitmap, 256, 256, false)
        val croppedBitmap = Bitmap.createBitmap(
            resizedBitmap,
            (resizedBitmap.width - 224) / 2,
            (resizedBitmap.height - 224) / 2,
            224,
            224
        )

        val byteBuffer = ByteBuffer.allocateDirect(4 * 1 * 3 * 224 * 224)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(224 * 224)
        croppedBitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)

        val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val std = floatArrayOf(0.229f, 0.224f, 0.225f)

        // Add the batch dimension in NCHW format
        for (c in 0 until 3) { // For each channel
            for (y in 0 until 224) {
                for (x in 0 until 224) {
                    val pixel = intValues[y * 224 + x]

                    val r = (pixel shr 16 and 0xFF) / 255.0f
                    val g = (pixel shr 8 and 0xFF) / 255.0f
                    val b = (pixel and 0xFF) / 255.0f

                    when (c) {
                        0 -> byteBuffer.putFloat((r - mean[c]) / std[c]) // R channel
                        1 -> byteBuffer.putFloat((g - mean[c]) / std[c]) // G channel
                        2 -> byteBuffer.putFloat((b - mean[c]) / std[c]) // B channel
                    }
                }
            }
        }

        return byteBuffer
    }

    fun loadLabels(context: Context, jsonPath: String): List<String> {
        val inputStream = context.assets.open(jsonPath)
        val json = inputStream.bufferedReader().use { it.readText() }
        return JSONObject(json).keys().asSequence().map { key ->
            JSONObject(json).getJSONObject(key).getString("kor") // "kor" 필드만 가져옴
        }.toList()
//        return JSONObject(json).keys().asSequence().map { JSONObject(json).getString(it) }.toList()
    }

    fun runInference(tfliteModelPath: String, bitmap: Bitmap, context: Context): FloatArray {
        val nnApiDelegate = NnApiDelegate()
        val options = Interpreter.Options().addDelegate(nnApiDelegate)
        val interpreter = Interpreter(FileUtil.loadMappedFile(context, tfliteModelPath), options)

        val inputBuffer = preprocess(bitmap)
        val outputBuffer = ByteBuffer.allocateDirect(4 * 367)
        outputBuffer.order(ByteOrder.nativeOrder())

        interpreter.run(inputBuffer, outputBuffer)
        outputBuffer.rewind()
        val probabilities = FloatArray(367)
        outputBuffer.asFloatBuffer().get(probabilities)

        nnApiDelegate.close()
        return probabilities
    }

    fun runDiseaseInference(tfliteModelPath: String, bitmap: Bitmap, context: Context): FloatArray {
        val nnApiDelegate = NnApiDelegate()
        val options = Interpreter.Options().addDelegate(nnApiDelegate)
        val interpreter = Interpreter(FileUtil.loadMappedFile(context, tfliteModelPath), options)

        val inputBuffer = diseasePreprocess(bitmap)
        val outputBuffer = ByteBuffer.allocateDirect(4 * 5)
        outputBuffer.order(ByteOrder.nativeOrder())

        interpreter.run(inputBuffer, outputBuffer)
        outputBuffer.rewind()
        val probabilities = FloatArray(5)
        outputBuffer.asFloatBuffer().get(probabilities)

        nnApiDelegate.close()
        return probabilities
    }

    fun softmax(logits: FloatArray): FloatArray {
        val expValues = logits.map { exp(it.toDouble()) }
        val sumExpValues = expValues.sum()
        return expValues.map { (it / sumExpValues).toFloat() }.toFloatArray()
    }

    fun getTopKResults(
        probabilities: FloatArray,
        labels: List<String>,
        topK: Int = 3
    ): List<ImageInferenceResult> {
        val topKIndices = probabilities.indices
            .sortedByDescending { probabilities[it] }
            .take(topK)
        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode.apply { RoundingMode.HALF_UP }

        return topKIndices.map { index ->
            val formattedProbability = decimalFormat.format(probabilities[index] * 100).toFloat()
            ImageInferenceResult(labels[index], formattedProbability)
        }
    }
}