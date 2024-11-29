package com.android.greenmate.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.greenmate.domain.repository.MyPlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyPlantUpdateViewModel @Inject constructor(
    private val myPlantRepository: MyPlantRepository
) : ViewModel() {
    var myPlantId by mutableLongStateOf(0L)

    private val _alias = MutableLiveData<String>()
    val alias: LiveData<String> get() = _alias

    private val _image = MutableLiveData<String>()
    val image: LiveData<String> get() = _image

    private val _favorite = MutableLiveData<Boolean>()
    val favorite: LiveData<Boolean> get() = _favorite


    fun updateMyPlantAlias(alias: String, myPlantId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val newAlias = alias
            myPlantRepository.updateMyPlantAlias(newAlias, myPlantId)
            val updateMyPlant = myPlantRepository.getMyPlantById(myPlantId)
            if (updateMyPlant != null) {
                _alias.postValue(updateMyPlant.alias)
                myPlantRepository.getFavoriteMyPlant()
                myPlantRepository.getAllMyPlants()
            }
        }
    }

    fun updateMyPlantFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            myPlantRepository.updateAndSetFavorite(true, myPlantId)
            val updateMyPlant = myPlantRepository.getMyPlantById(myPlantId)
            if (updateMyPlant != null) {
                _favorite.postValue(updateMyPlant.favorite)
                myPlantRepository.getFavoriteMyPlant()
                myPlantRepository.getAllMyPlants()
            }
        }
    }
}