package id.dev.snoozeloo.alarm.presentation.settings

sealed interface AlarmSettingEvent {
    data class OnAlarmSuccessCreateOrUpdate(val id: Int, val alarmTime: String): AlarmSettingEvent
}