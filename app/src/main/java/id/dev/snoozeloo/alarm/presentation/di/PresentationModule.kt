package id.dev.snoozeloo.alarm.presentation.di

import id.dev.snoozeloo.alarm.presentation.list.AlarmListViewModel
import id.dev.snoozeloo.alarm.presentation.settings.AlarmSettingsViewModel
import id.dev.snoozeloo.alarm.presentation.trigger.AlarmTriggerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::AlarmListViewModel)
    viewModelOf(::AlarmSettingsViewModel)
    viewModelOf(::AlarmTriggerViewModel)
}