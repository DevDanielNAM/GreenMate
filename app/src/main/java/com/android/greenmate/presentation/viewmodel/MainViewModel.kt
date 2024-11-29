package com.android.greenmate.presentation.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.greenmate.data.model.ImageInferenceResult
import com.android.greenmate.domain.usecase.RunDiseaseInferenceUseCase
import com.android.greenmate.domain.usecase.RunInferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val runInferenceUseCase: RunInferenceUseCase,
    private val runDiseaseInferenceUseCase: RunDiseaseInferenceUseCase
) : ViewModel() {

    // Compose에서 관찰 가능한 state
    private val _inferenceResults = mutableStateOf<List<ImageInferenceResult>>(emptyList())
    val inferenceResults: State<List<ImageInferenceResult>> = _inferenceResults

    private val _inferenceDiseaseResults = mutableStateOf<List<ImageInferenceResult>>(emptyList())
    val inferenceDiseaseResults: State<List<ImageInferenceResult>> = _inferenceDiseaseResults

    fun runInference(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = runInferenceUseCase(bitmap)
            _inferenceResults.value = results // State 업데이트
        }
    }

    fun runDiseaseInference(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = runDiseaseInferenceUseCase(bitmap)
            _inferenceDiseaseResults.value = results // State 업데이트
        }
    }
}
