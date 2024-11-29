package com.android.greenmate.domain.usecase

import android.graphics.Bitmap
import com.android.greenmate.data.model.ImageInferenceResult
import com.android.greenmate.domain.repository.ImageInferenceRepository

class RunInferenceUseCase(
    private val repository: ImageInferenceRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): List<ImageInferenceResult> {
        return repository.runInference(bitmap)
    }
}