package com.android.greenmate.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.greenmate.data.datasource.local.dao.RecordDao
import com.android.greenmate.data.mapper.toDomainModel
import com.android.greenmate.domain.model.Records
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordDao: RecordDao
) : ViewModel() {
    private val _recordValues = MutableStateFlow<List<Records>>(emptyList())
    val recordValues: StateFlow<List<Records>> = _recordValues.asStateFlow()


    init {
        getAllRecords()
    }

    fun getAllRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            val records = recordDao.getAllRecords()
            _recordValues.value = (records.map { it.toDomainModel() })
        }
    }

    fun checkRecordExistsForDateSync(date: LocalDate): Boolean {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        return runBlocking {
            recordDao.doesRecordExistForDate(startOfDay, endOfDay)
        }
    }

}