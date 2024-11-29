package com.android.greenmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.greenmate.data.datasource.local.dao.ModuleDao
import com.android.greenmate.data.mapper.toDomainModel
import com.android.greenmate.domain.model.Module
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModuleViewModel @Inject constructor(
    private val moduleDao: ModuleDao
) : ViewModel() {

    // LiveData to hold the list of room names
    private val _moduleValues = MutableLiveData<List<Module>>()
    val moduleValues: LiveData<List<Module>> get() = _moduleValues

    private val _soilMoistures = MutableLiveData<List<Float>>()
    val soilMoistures: LiveData<List<Float>> get() = _soilMoistures


    fun getModulesByMyPlantId(myPlantId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val modules = moduleDao.getModulesByMyPlantId(myPlantId)
            _moduleValues.postValue(modules.map { it.toDomainModel() })
            _soilMoistures.postValue(modules.map { it.soilMoisture })
        }
    }

    fun getSoilModulesByMyPlantId(myPlantId: Long): LiveData<List<Float>> {
        val soilMoisturesLiveData = MutableLiveData<List<Float>>()
        viewModelScope.launch(Dispatchers.IO) {
            val modules = moduleDao.getModulesByMyPlantId(myPlantId)
            val soilMoistures = modules.map { it.soilMoisture }
            soilMoisturesLiveData.postValue(soilMoistures)
        }
        return soilMoisturesLiveData
    }
}