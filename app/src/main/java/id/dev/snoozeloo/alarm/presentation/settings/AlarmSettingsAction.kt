package id.dev.snoozeloo.alarm.presentation.settings

import android.net.Uri
import id.dev.snoozeloo.core.presentation.RepeatDayEnum

sealed interface AlarmSettingsAction {
    data object OnSaveAlarmClick : AlarmSettingsAction
    data object OnCloseAlarmClick : AlarmSettingsAction
    data object OnCloseRingtoneClick : AlarmSettingsAction
    data class OnAlarmHoursChange(val hours: String) : AlarmSettingsAction
    data class OnAlarmMinutesChange(val minutes: String) : AlarmSettingsAction
    data object OnAlarmNameClick : AlarmSettingsAction
    data class OnAlarmNameChange(val alarmNewName: String) : AlarmSettingsAction
    data class OnRepeatClick(val selectedDay: RepeatDayEnum) : AlarmSettingsAction
    data object OnAlarmRingtoneClick : AlarmSettingsAction
    data class OnAlarmVolumeChange(val volume: Float) : AlarmSettingsAction
    data class OnVibrateChange(val value: Boolean) : AlarmSettingsAction
    data class OnAvailableRingtoneChange(val title: String, val uri: Uri) : AlarmSettingsAction
    data class OnDoneLoadAllRingtones(val value: Boolean) : AlarmSettingsAction
    data class OnAlarmRingtoneChange(val title: String, val uri: Uri) : AlarmSettingsAction
}