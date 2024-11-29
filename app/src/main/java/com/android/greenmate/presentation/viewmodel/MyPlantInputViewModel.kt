package com.android.greenmate.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.greenmate.domain.model.MyPlant
import com.android.greenmate.domain.repository.MyPlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MyPlantInputViewModel @Inject constructor(
    private val myPlantRepository: MyPlantRepository
) : ViewModel() {
    var plantId by mutableLongStateOf(0L)
    var category by mutableStateOf("")
    var alias by mutableStateOf("")
    var image by mutableStateOf("")
    var date by mutableStateOf(Date())

    fun insertMyPlant() {
        viewModelScope.launch(Dispatchers.IO) {
            val newMyPlant = MyPlant(
                plantId = plantId,
                category = category,
                alias = alias,
                image = image,
                favorite = true,
                date = date
            )
            myPlantRepository.insertAndSetFavorite(newMyPlant)
        }
    }
}