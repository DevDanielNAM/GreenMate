package com.android.greenmate.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.greenmate.domain.model.Module
import com.android.greenmate.domain.repository.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class ModuleInputViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository
) : ViewModel() {
    var myPlantId by mutableStateOf(0L)
    var lightIntensity by mutableStateOf(0f)
    var temperature by mutableStateOf(0f)
    var humidity by mutableStateOf(0f)
    var soilMoisture by mutableStateOf(0f)
    var timestamp by mutableStateOf(Date())

    fun insertModule() {
        viewModelScope.launch(Dispatchers.IO) {
            val newModule = Module(
                myPlantId = myPlantId,
                lightIntensity = lightIntensity,
                temperature = temperature,
                humidity = humidity,
                soilMoisture = soilMoisture,
                timestamp = timestamp
            )
            moduleRepository.insertModule(newModule)
        }
    }
}
