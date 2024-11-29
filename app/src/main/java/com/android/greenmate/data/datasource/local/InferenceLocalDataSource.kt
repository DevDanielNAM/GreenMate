package com.android.greenmate.data.datasource.local


import android.content.Context
import android.graphics.Bitmap
import com.android.greenmate.data.model.ImageInferenceResult
import com.android.greenmate.utils.TensorFlowLiteUtils

class InferenceLocalDataSource(private val context: Context) {
    fun runInference(bitmap: Bitmap): List<ImageInferenceResult> {
        val labels = TensorFlowLiteUtils.loadLabels(context, "greenmate_plants_id.json")
        val logits = TensorFlowLiteUtils.runInference("greenmate_plants_mobilenet_256_1003.tflite", bitmap, context)
        val probabilities = TensorFlowLiteUtils.softmax(logits)
        return TensorFlowLiteUtils.getTopKResults(probabilities, labels)
    }

    fun runDiseaseInference(bitmap: Bitmap): List<ImageInferenceResult> {
        val labels = TensorFlowLiteUtils.loadLabels(context, "greenmate_disease_id.json")
        val logits = TensorFlowLiteUtils.runDiseaseInference("greenmate_disease_mobilenet_v3.tflite", bitmap, context)
        val probabilities = TensorFlowLiteUtils.softmax(logits)
        return TensorFlowLiteUtils.getTopKResults(probabilities, labels)
    }
}