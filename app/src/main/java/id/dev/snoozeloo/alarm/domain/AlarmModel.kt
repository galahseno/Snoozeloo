package id.dev.snoozeloo.alarm.domain

data class AlarmModel(
    val id: Int? = null,
    val alarmTime: String,
    val alarmName: String,
    val snoozedTime: String = "",
    val isActive: Boolean,
    val selectedDays: List<Int>,
    val alarmRingtone: String,
    val alarmRingtoneUri: String,
    val alarmVolume: Float,
    val isVibrate: Boolean,
)
