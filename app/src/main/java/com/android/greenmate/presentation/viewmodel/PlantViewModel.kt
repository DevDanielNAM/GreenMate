package com.android.greenmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.greenmate.data.datasource.local.dao.PlantDao
import com.android.greenmate.data.datasource.local.entity.DiseaseEntity
import com.android.greenmate.data.mapper.toDomainModel
import com.android.greenmate.domain.model.Plant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val plantDao: PlantDao
) : ViewModel() {

    private val _plantId = MutableLiveData<Long?>()
    val plantId: LiveData<Long?> get() = _plantId

    private val _plantIds = MutableLiveData<MutableMap<String, Long>>(mutableMapOf())
    val plantIds: MutableLiveData<MutableMap<String, Long>> get() = _plantIds

    private val _plantCategories = MutableLiveData<MutableMap<String, String>>(mutableMapOf())
    val plantCategories: MutableLiveData<MutableMap<String, String>> get() = _plantCategories

    private val _title = MutableLiveData<String>()
    val title: MutableLiveData<String> get() = _title

    private val _light = MutableLiveData<String>()
    val light: MutableLiveData<String> get() = _light

    private val _soil = MutableLiveData<Float>()
    val soil: MutableLiveData<Float> get() = _soil

    private val _humidity = MutableLiveData<String>()
    val humidity: MutableLiveData<String> get() = _humidity

    private val _temperature = MutableLiveData<String>()
    val temperature: MutableLiveData<String> get() = _temperature

    private val _water = MutableLiveData<String>()
    val water: MutableLiveData<String> get() = _water


    private val _descriptions = MutableLiveData<MutableMap<String, String>>(mutableMapOf())
    val descriptions: MutableLiveData<MutableMap<String, String>> get() = _descriptions

    private val _description = MutableLiveData<String>()
    val description: MutableLiveData<String> get() = _description


    private val _plantInfo = MutableStateFlow<Plant?>(null)
    val plantInfo: StateFlow<Plant?> = _plantInfo.asStateFlow()


    private val _plantDisease = MutableStateFlow<List<DiseaseEntity>>(emptyList())
    val plantDisease: StateFlow<List<DiseaseEntity>> = _plantDisease.asStateFlow()


    private val _tPlantId = MutableLiveData<List<Long>>()
    val tPlantId: MutableLiveData<List<Long>> get() = _tPlantId

    private val _tCategory = MutableLiveData<List<String>>()
    val tCategory: MutableLiveData<List<String>> get() = _tCategory

    private val _tTitle = MutableLiveData<List<String>>()
    val tTitle: MutableLiveData<List<String>> get() = _tTitle

    private val _tDescription = MutableLiveData<List<String>>()
    val tDescription: MutableLiveData<List<String>> get() = _tDescription

    private val _tLight = MutableLiveData<List<String>>()
    val tLight: MutableLiveData<List<String>> get() = _tLight

    private val _tWater = MutableLiveData<List<String>>()
    val tWater: MutableLiveData<List<String>> get() = _tWater

    private val _tHumidity = MutableLiveData<List<String>>()
    val tHumidity: MutableLiveData<List<String>> get() = _tHumidity

    private val _tTemperature = MutableLiveData<List<String>>()
    val tTemperature: MutableLiveData<List<String>> get() = _tTemperature

    private val _tPlantImage = MutableLiveData<List<String>>()
    val tPlantImage: MutableLiveData<List<String>> get() = _tPlantImage

    private val _plantImages = MutableLiveData<MutableMap<String, String>>(mutableMapOf())
    val plantImages: MutableLiveData<MutableMap<String, String>> get() = _plantImages

    private val _plantImage = MutableLiveData<String>()
    val plantImage: MutableLiveData<String> get() = _plantImage

    init{}

    fun getPlantById(plantId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val plant = plantDao.getPlantById(plantId)
            plant?.let {
                _title.postValue(it.korName)
                _description.postValue(it.description)
                _humidity.postValue(it.humidity)
                _temperature.postValue(it.temperature)
                _light.postValue(it.light)
                _water.postValue(it.water)
            }
        }
    }

    fun getPlantInfoById(plantId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val plant = plantDao.getPlantById(plantId)
            plant?.let {
                _plantInfo.value = (it.toDomainModel())
            }
        }
    }

    fun getPlantByTitle(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val plant = plantDao.getPlantByTitle(title)
            plant?.let {
                // 현재 descriptions 값을 가져와서 mutableMapOf로 변환
                val currentDescriptions = _descriptions.value ?: mutableMapOf()
                currentDescriptions[title] = it.description
                val currentPlantImages = _plantImages.value ?: mutableMapOf()
                currentPlantImages[title] = it.image
                val currentPlantIds = _plantIds.value ?: mutableMapOf()
                currentPlantIds[title] = it.plantId
                val currentPlantCategories = _plantCategories.value ?: mutableMapOf()
                currentPlantCategories[title] = it.category
                // LiveData 업데이트
                _descriptions.postValue(currentDescriptions)
                _plantImages.postValue(currentPlantImages)
                _plantIds.postValue(currentPlantIds)
                _plantCategories.postValue(currentPlantCategories)
            }
            val plants = plantDao.getPlantByPartialTitle(title)
            if (plants.isNotEmpty()) {
                _tPlantId.postValue(plants.map { it.plantId })
                _tCategory.postValue(plants.map { it.category })
                _tTitle.postValue(plants.map { it.korName })
                _tDescription.postValue(plants.map { it.description })
                _tLight.postValue(plants.map { it.light })
                _tWater.postValue(plants.map { it.water })
                _tHumidity.postValue(plants.map { it.humidity })
                _tTemperature.postValue(plants.map { it.temperature })
                _tPlantImage.postValue(plants.map { it.image })
            } else {
                _tPlantId.postValue(listOf(0L))
                _tCategory.postValue(listOf(""))
                _tTitle.postValue(listOf("일치하는 주인님이 없어요"))
                _tDescription.postValue(listOf("주인님 설명서가 없어요"))
                _tLight.postValue(listOf(""))
                _tWater.postValue(listOf(""))
                _tHumidity.postValue(listOf(""))
                _tTemperature.postValue(listOf(""))
                _tPlantImage.postValue(listOf("no_image"))
            }
        }
    }

    fun getCategoryByTitle(title: String): String? {
        return _plantCategories.value?.get(title)
    }

    fun getDescriptionByTitle(title: String): String? {
        return _descriptions.value?.get(title)
    }

    fun getPlantImageByTitle(title: String): String? {
        return _plantImages.value?.get(title)
    }

    fun getPlantIdByTitle(title: String): Long? {
        return _plantIds.value?.get(title)
    }

    fun getDiseasesByPlantId(plantId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val plantDisease = plantDao.getDiseasesByPlantId(plantId)
            _plantDisease.value = (plantDisease)
        }
    }
}
