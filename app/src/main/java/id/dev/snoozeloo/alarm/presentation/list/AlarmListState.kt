package id.dev.snoozeloo.alarm.presentation.list

import id.dev.snoozeloo.alarm.presentation.model.AlarmState

data class AlarmListState(
    val alarms: List<AlarmState> = listOf(),
)
