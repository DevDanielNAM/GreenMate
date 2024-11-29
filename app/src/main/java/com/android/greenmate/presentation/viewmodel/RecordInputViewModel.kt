package com.android.greenmate.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.greenmate.domain.model.Records
import com.android.greenmate.domain.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RecordInputViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {
    val myPlantId = MutableLiveData<Long>(0L)
    val title = MutableLiveData<String>("")
    val content = MutableLiveData<String>("")
    val image = MutableLiveData<String>("null")
    val date = MutableLiveData<Date>(Date())

    fun insertRecord() {
        viewModelScope.launch(Dispatchers.IO) {
            val newRecord = Records(
                myPlantId = myPlantId.value ?: 0L,
                title = title.value ?: "",
                content = content.value ?: "",
                image = image.value ?: "",
                date = date.value ?: Date()
            )
            recordRepository.insertRecord(newRecord)
        }
    }
}