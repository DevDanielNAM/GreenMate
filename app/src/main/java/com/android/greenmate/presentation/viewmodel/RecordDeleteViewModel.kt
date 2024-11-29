package com.android.greenmate.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.greenmate.domain.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RecordDeleteViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _recordDeleted = MutableLiveData<Boolean>(false)
    val recordDeleted: LiveData<Boolean> = _recordDeleted

    var myPlantId by mutableLongStateOf(0L)
    var title by mutableStateOf("")
    var content by mutableStateOf("")
    var image by mutableStateOf("")
    var date by mutableStateOf(Date())

    fun deleteRecord() {
        viewModelScope.launch(Dispatchers.IO) {
                recordRepository.deleteRecord(
                    myPlantId = myPlantId,
                    title = title,
                    date = date)
                _recordDeleted.postValue(true)
        }
    }

    fun resetRecordDeletedState() {
        _recordDeleted.value = false
    }
}