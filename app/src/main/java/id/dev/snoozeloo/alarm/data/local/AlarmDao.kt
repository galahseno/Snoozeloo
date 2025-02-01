package id.dev.snoozeloo.alarm.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarmEntity: AlarmEntity): Long

    @Query("UPDATE alarm_table SET isActive = :isActive WHERE id = :id")
    suspend fun changeAlarmStatus(id: Int, isActive: Boolean)

    @Update
    suspend fun modifyAlarmSettings(alarmEntity: AlarmEntity)

    @Query("SELECT * FROM alarm_table")
    fun getAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarm_table where id=:id")
    suspend fun getAlarmById(id: Int): AlarmEntity
}