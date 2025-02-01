package id.dev.snoozeloo.alarm.data

import id.dev.snoozeloo.alarm.data.local.AlarmDao
import id.dev.snoozeloo.alarm.domain.AlarmModel
import id.dev.snoozeloo.alarm.domain.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao
) : AlarmRepository {
    override suspend fun insertAlarm(alarmModel: AlarmModel): Int {
        return alarmDao.insertAlarm(alarmModel.toAlarmEntity()).toInt()
    }

    override suspend fun changeAlarmStatus(id: Int, isActive: Boolean) {
        alarmDao.changeAlarmStatus(id, isActive)
    }

    override suspend fun modifyAlarmSettings(alarmModel: AlarmModel) {
        alarmDao.modifyAlarmSettings(alarmModel.toAlarmEntity())
    }

    override fun getAlarms(): Flow<List<AlarmModel>> {
        return alarmDao.getAlarms().map { listAlarm ->
            listAlarm.map { it.toAlarmModel() }
        }
    }

    override suspend fun getAlarmById(id: Int): AlarmModel {
        return alarmDao.getAlarmById(id).toAlarmModel()
    }
}