package com.android.greenmate.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.greenmate.data.datasource.local.dao.AlarmDao
import com.android.greenmate.data.datasource.local.entity.AlarmEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmDao: AlarmDao,
) : ViewModel() {

    private val _alarms = MutableLiveData<List<AlarmEntity>>()
    val alarms: LiveData<List<AlarmEntity>> = _alarms


    fun loadAlarms(myPlantId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val alarmsList = alarmDao.getAlarmsByMyPlantId(myPlantId)
            _alarms.postValue(alarmsList)
        }
    }

    fun addReminder(myPlantId: Long, title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newAlarm = AlarmEntity(
                myPlantId = myPlantId,
                title = title,
//                cycle = "0",
                isDone = false
            )
            alarmDao.insertAlarm(newAlarm)
            loadAlarms(myPlantId) // Reload alarms after saving
        }
    }

    fun saveOrUpdateAlarm(alarm: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingAlarm = alarmDao.getAlarmByTitle(alarm.title)
            if (existingAlarm != null) {
                // Update the existing alarm
                val updatedAlarm = alarm.copy(alarmId = existingAlarm.alarmId)
                alarmDao.updateAlarm(updatedAlarm)
            } else {
                // Insert a new alarm
                alarmDao.insertAlarm(alarm)
            }
            loadAlarms(alarm.myPlantId)
        }
    }

    fun updateAlarmDone(isDone: Boolean, myPlantId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.updateAlarmDone(isDone, myPlantId)
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.deleteAlarm(alarm)
            loadAlarms(alarm.myPlantId)  // Reload alarms after deleting
        }
    }
}
