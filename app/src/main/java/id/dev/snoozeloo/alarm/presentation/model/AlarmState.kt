package id.dev.snoozeloo.alarm.presentation.model

import id.dev.snoozeloo.core.presentation.RepeatDayEnum
import java.time.Duration
import java.util.Calendar

data class AlarmState(
    val id: Int? = null,
    val alarmName: String? = null,
    val isActive: Boolean = false,
    val alarmTime: String = "",
    val alarmForTime: Duration = Duration.ZERO,
    val timeToBed: String = "",
    val selectedDays: List<Pair<RepeatDayEnum, Boolean>> = RepeatDayEnum.entries.map { dayEnum ->
        Pair(dayEnum, dayEnum.day in Calendar.MONDAY..Calendar.FRIDAY)
    }
)
