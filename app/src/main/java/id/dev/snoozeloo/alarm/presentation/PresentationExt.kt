package id.dev.snoozeloo.alarm.presentation

import id.dev.snoozeloo.alarm.domain.AlarmModel
import id.dev.snoozeloo.alarm.presentation.model.AlarmState
import id.dev.snoozeloo.alarm.presentation.settings.AlarmSettingState
import id.dev.snoozeloo.alarm.presentation.trigger.AlarmTriggerState
import id.dev.snoozeloo.core.presentation.RepeatDayEnum
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun AlarmSettingState.toAlarmModel() = AlarmModel(
    id = id,
    alarmTime = "$alarmHours:$alarmMinutes",
    alarmName = alarmName,
    isActive = true,
    selectedDays = selectedDays.filter { it.second }.map { it.first.day },
    alarmRingtone = alarmRingtone,
    alarmRingtoneUri = alarmRingtoneUri,
    alarmVolume = alarmVolume,
    isVibrate = isVibrate,
)

fun AlarmTriggerState.toAlarmModel() = AlarmModel(
    id = id,
    alarmTime = alarmTime,
    alarmName = alarmName,
    snoozedTime = snoozedTime,
    isActive = true,
    selectedDays = selectedDays,
    alarmRingtone = alarmRingtone,
    alarmRingtoneUri = alarmRingtoneUri,
    alarmVolume = alarmVolume,
    isVibrate = isVibrate,
)

fun AlarmModel.toAlarmListState(): AlarmState {
    val now = ZonedDateTime.now()
    val alarmLocalTime = parse24HourToLocalTime(alarmTime)
    val alarmDateTime = now.with(alarmLocalTime)
        .let { if (it.isBefore(now)) it.plusDays(1) else it }
    val duration = Duration.between(now, alarmDateTime)

    return AlarmState(
        id = id,
        alarmName = alarmName,
        isActive = isActive,
        alarmTime = alarmLocalTime.format(
            DateTimeFormatter.ofPattern(
                "hh:mm a",
                Locale.ROOT
            )
        ),
        alarmForTime = duration,
        timeToBed = alarmLocalTime.minusHours(8)
            .format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ROOT)),
        selectedDays = RepeatDayEnum.entries.map { dayEnum ->
            Pair(dayEnum, dayEnum.day in selectedDays)
        },
    )
}

fun parse24HourToLocalTime(timeString: String): LocalTime {
    return LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
}

fun formatDuration(duration: Duration): String {
    val hours = duration.toHoursPart()
    val minutes = duration.toMinutesPart()
    val seconds = duration.toSecondsPart()
    return if (hours > 0) {
        "${hours}h ${minutes}min ${seconds}s"
    } else if (minutes > 0) {
        "${minutes}min ${seconds}s"
    } else {
        "less than 1 minutes"
    }
}

fun chunkedSelectedAndNotSelectedDays(dayPairs: List<Pair<RepeatDayEnum, Boolean>>): List<List<Pair<RepeatDayEnum, Boolean>>> {
    val result = mutableListOf<List<Pair<RepeatDayEnum, Boolean>>>()
    val currentGroup = mutableListOf<Pair<RepeatDayEnum, Boolean>>()
    var currentGroupSelected = false

    for (pair in dayPairs) {
        if (currentGroup.isEmpty()) {
            // Start a new group
            currentGroup.add(pair)
            currentGroupSelected = pair.second
        } else {
            if (pair.second == currentGroupSelected) {
                // Continue the current group
                currentGroup.add(pair)
            } else {
                // Start a new group
                result.add(currentGroup.toList())
                currentGroup.clear()
                currentGroup.add(pair)
                currentGroupSelected = pair.second
            }
        }
    }

    // Add the last group if it's not empty
    if (currentGroup.isNotEmpty()) {
        result.add(currentGroup.toList())
    }

    return result
}