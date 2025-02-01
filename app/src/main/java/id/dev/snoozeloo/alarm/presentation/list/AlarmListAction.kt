package id.dev.snoozeloo.alarm.presentation.list

sealed interface AlarmListAction {
    data class OnActiveAlarmChange(val id: Int?, val value: Boolean) : AlarmListAction
    data class OnAlarmClick(val id: Int?) : AlarmListAction
}