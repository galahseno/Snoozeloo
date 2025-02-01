package id.dev.snoozeloo.alarm.domain

import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    suspend fun insertAlarm(alarmModel: AlarmModel): Int
    suspend fun changeAlarmStatus(id: Int, isActive: Boolean)
    suspend fun modifyAlarmSettings(alarmModel: AlarmModel)
    fun getAlarms(): Flow<List<AlarmModel>>
    suspend fun getAlarmById(id: Int): AlarmModel
}