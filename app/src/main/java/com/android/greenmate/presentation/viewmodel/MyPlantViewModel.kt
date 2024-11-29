package com.android.greenmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.greenmate.data.datasource.local.dao.MyPlantDao
import com.android.greenmate.data.mapper.toDomainModel
import com.android.greenmate.domain.model.MyPlant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MyPlantViewModel @Inject constructor(
    private val myPlantDao: MyPlantDao
) : ViewModel() {

    private val _favoritePlantId = MutableLiveData<Long>()
    val favoritePlantId: LiveData<Long> get() = _favoritePlantId

    private val _favoriteMyPlantId = MutableLiveData<Long>()
    val favoriteMyPlantId: LiveData<Long> get() = _favoriteMyPlantId

    private val _favoriteMyPlantCategory = MutableLiveData<String>()
    val favoriteMyPlantCategory: LiveData<String> get() = _favoriteMyPlantCategory

    private val _favoriteMyPlantAlias = MutableLiveData<String>()
    val favoriteMyPlantAlias: LiveData<String> get() = _favoriteMyPlantAlias

    private val _favoriteMyPlantImage = MutableLiveData<String>()
    val favoriteMyPlantImage: LiveData<String> get() = _favoriteMyPlantImage

    private val _favoriteMyPlantDate = MutableLiveData<Date>()
    val favoriteMyPlantDate: LiveData<Date> get() = _favoriteMyPlantDate

    private val _existMyPlants = MutableLiveData<Boolean>()
    val existMyPlants: LiveData<Boolean> get() = _existMyPlants

    private val _myPlantId = MutableLiveData<Long>()
    val myPlantId: LiveData<Long> get() = _myPlantId

    private val _myPlantIds = MutableLiveData<List<Long>>()
    val myPlantIds: LiveData<List<Long>> get() = _myPlantIds

    private val _plantId = MutableLiveData<List<Long>>()
    val plantId: LiveData<List<Long>> get() = _plantId

    private val _aliases = MutableLiveData<List<String>>()
    val aliases: LiveData<List<String>> get() = _aliases

    private val _myPlantAlias = MutableLiveData<String>()
    val myPlantAlias: LiveData<String> get() = _myPlantAlias

    private val _images = MutableLiveData<List<String>>()
    val myPlantImages: LiveData<List<String>> get() = _images

    private val _favorites = MutableLiveData<List<Boolean>>()
    val favorites: LiveData<List<Boolean>> get() = _favorites

    private val _myPlants = MutableLiveData<List<MyPlant>>(emptyList())
    val myPlants: LiveData<List<MyPlant>> get() = _myPlants

    init {
        getAllMyPlants()
        getFavoriteMyPlant()
    }

    fun getAllMyPlants() {
        viewModelScope.launch(Dispatchers.IO) {
            val myPlants = myPlantDao.getAllMyPlants()
            if (myPlants.isNotEmpty()) {
                _myPlantIds.postValue(myPlants.map { it.myPlantId })
                _aliases.postValue(myPlants.map { it.alias })
                _images.postValue(myPlants.map { it.image })
                _favorites.postValue(myPlants.map { it.favorite })
                _myPlants.postValue((myPlants.map { it.toDomainModel() }))
                _existMyPlants.postValue(true)
            } else {
                _existMyPlants.postValue(false)
            }
        }
    }

    fun getFavoriteMyPlant(){
        viewModelScope.launch(Dispatchers.IO) {
            val favoriteMyPlant = myPlantDao.getFavoriteMyPlant()
            if (favoriteMyPlant != null) {
                _favoritePlantId.postValue(favoriteMyPlant.plantId)
                _favoriteMyPlantId.postValue(favoriteMyPlant.myPlantId)
                _favoriteMyPlantCategory.postValue(favoriteMyPlant.category)
                _favoriteMyPlantAlias.postValue(favoriteMyPlant.alias)
                _favoriteMyPlantImage.postValue(favoriteMyPlant.image)
                _favoriteMyPlantDate.postValue(favoriteMyPlant.date)
            }
        }
    }
}
