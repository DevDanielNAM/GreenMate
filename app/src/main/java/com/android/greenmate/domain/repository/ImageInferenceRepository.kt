package com.android.greenmate.domain.repository

import android.graphics.Bitmap
import com.android.greenmate.data.model.ImageInferenceResult

interface ImageInferenceRepository {
    suspend fun runInference(bitmap: Bitmap): List<ImageInferenceResult>
    suspend fun runDiseaseInference(bitmap: Bitmap): List<ImageInferenceResult>
}
