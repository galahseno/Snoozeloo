package id.dev.snoozeloo.alarm.presentation.trigger

data class AlarmTriggerState(
    val id: Int? = null,
    val alarmTime: String = "",
    val alarmName: String = "",
    val snoozedTime: String = "",
    val isActive: Boolean = false,
    val selectedDays: List<Int> = listOf(),
    val alarmRingtone: String = "",
    val alarmRingtoneUri: String = "",
    val alarmVolume: Float = 0f,
    val isVibrate: Boolean = false,
    val isSnoozed: Boolean = false
)
