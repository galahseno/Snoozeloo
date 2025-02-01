package id.dev.snoozeloo.alarm.presentation.trigger

sealed interface AlarmTriggerAction {
    data object OnTurnOffClicked : AlarmTriggerAction
    data object OnSnoozeClicked : AlarmTriggerAction
}