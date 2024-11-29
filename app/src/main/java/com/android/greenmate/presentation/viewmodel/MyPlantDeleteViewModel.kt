package com.android.greenmate.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.greenmate.domain.repository.MyPlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPlantDeleteViewModel @Inject constructor(
    private val myPlantRepository: MyPlantRepository
) : ViewModel() {

    var myPlantId by mutableLongStateOf(-1L)

    private val _plantDeleted = mutableStateOf(false)
    val plantDeleted: State<Boolean> = _plantDeleted

    fun deleteMyPlantByMyPlantId() {
        viewModelScope.launch(Dispatchers.IO) {
            myPlantRepository.deleteMyPlantByMyPlantId(myPlantId)
            _plantDeleted.value = true
        }
    }

    fun resetPlantDeletedState() {
        _plantDeleted.value = false
    }
}