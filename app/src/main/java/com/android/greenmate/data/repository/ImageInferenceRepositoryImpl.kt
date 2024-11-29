package com.android.greenmate.data.repository

import android.graphics.Bitmap
import com.android.greenmate.data.datasource.local.InferenceLocalDataSource
import com.android.greenmate.data.model.ImageInferenceResult
import com.android.greenmate.domain.repository.ImageInferenceRepository

class ImageInferenceRepositoryImpl(
    private val localDataSource: InferenceLocalDataSource
) : ImageInferenceRepository {

    override suspend fun runInference(bitmap: Bitmap): List<ImageInferenceResult> {
        return localDataSource.runInference(bitmap)
    }

    override suspend fun runDiseaseInference(bitmap: Bitmap): List<ImageInferenceResult> {
        return localDataSource.runDiseaseInference(bitmap)
    }
}
