package id.dev.snoozeloo.alarm.presentation.settings

import android.net.Uri
import id.dev.snoozeloo.core.presentation.RepeatDayEnum
import java.util.Calendar

data class AlarmSettingState(
    val id: Int? = null,
    val alarmTime: String = "",
    val alarmHours: String = "",
    val alarmMinutes: String = "",
    val alarmInText: String = "",
    val alarmName: String = "Work",
    val selectedDays: List<Pair<RepeatDayEnum, Boolean>> = RepeatDayEnum.entries.map { dayEnum ->
        Pair(dayEnum, dayEnum.day in Calendar.MONDAY..Calendar.FRIDAY)
    },
    val alarmRingtone: String = "Default",
    val alarmRingtoneUri: String = "",
    val isLoadingRingtones: Boolean = true,
    val availableRingtone: List<Pair<String, Uri>> = listOf(Pair("Silent", Uri.EMPTY)),
    val alarmVolume: Float = 0.5f,
    val isVibrate: Boolean = true,
    val isSavedEnable: Boolean = false,
)
