package id.dev.snoozeloo.alarm.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_table")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val alarmTime: String,
    val snoozedTime: String = "",
    val alarmName: String,
    val isActive: Boolean,
    val selectedDays: List<Int>,
    val alarmRingtone: String,
    val alarmRingtoneUri: String,
    val alarmVolume: Float,
    val isVibrate: Boolean,
)
