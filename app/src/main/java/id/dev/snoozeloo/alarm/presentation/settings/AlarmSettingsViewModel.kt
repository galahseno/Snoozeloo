package id.dev.snoozeloo.alarm.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.snoozeloo.alarm.domain.AlarmRepository
import id.dev.snoozeloo.alarm.presentation.parse24HourToLocalTime
import id.dev.snoozeloo.alarm.presentation.toAlarmModel
import id.dev.snoozeloo.core.presentation.RepeatDayEnum
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime

class AlarmSettingsViewModel(
    private val id: Int,
    private val alarmRepository: AlarmRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AlarmSettingState())
    val state = _state
        .onStart {
            if (id != -1) {
                val alarmSettings = alarmRepository.getAlarmById(id)
                _state.update {
                    it.copy(
                        id = alarmSettings.id,
                        alarmName = alarmSettings.alarmName,
                        alarmHours = alarmSettings.alarmTime.substringBefore(":"),
                        alarmMinutes = alarmSettings.alarmTime.substringAfter(":"),
                        alarmRingtone = alarmSettings.alarmRingtone,
                        alarmRingtoneUri = alarmSettings.alarmRingtoneUri,
                        alarmVolume = alarmSettings.alarmVolume,
                        isVibrate = alarmSettings.isVibrate,
                        selectedDays = RepeatDayEnum.entries.map { dayEnum ->
                            Pair(dayEnum, dayEnum.day in alarmSettings.selectedDays)
                        },
                    )
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            AlarmSettingState()
        )

    private val _event = Channel<AlarmSettingEvent>()
    val event = _event.receiveAsFlow()

    init {
        _state
            .map { it.alarmHours to it.alarmMinutes }
            .distinctUntilChanged()
            .onEach {
                if (it.first.isValidHour() && it.second.isValidMinute()) {
                    val now = ZonedDateTime.now()
                    val alarmTime = LocalTime.of(it.first.toInt(), it.second.toInt())
                    val alarmDateTime = now.with(alarmTime)
                        .let { zoneDateTime ->
                            if (zoneDateTime.isBefore(now)) zoneDateTime.plusDays(1) else zoneDateTime
                        }

                    val duration = Duration.between(now, alarmDateTime)
                    _state.update { state ->
                        state.copy(
                            alarmInText = "${duration.toHoursPart()}h ${duration.toMinutesPart()}min",
                            isSavedEnable = true
                        )
                    }
                } else {
                    _state.update { state ->
                        state.copy(
                            alarmInText = "",
                            isSavedEnable = false
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onAction(action: AlarmSettingsAction) {
        when (action) {
            is AlarmSettingsAction.OnSaveAlarmClick -> {
                viewModelScope.launch {
                    val id = if (_state.value.id != null) {
                        alarmRepository.modifyAlarmSettings(_state.value.toAlarmModel())
                        _state.value.id!!
                    } else {
                        _state.update { it.copy(isSavedEnable = false) }
                        alarmRepository.insertAlarm(_state.value.toAlarmModel())
                    }
                    _state.update { state ->
                        val alarmLocalTime =
                            parse24HourToLocalTime("${state.alarmHours}:${state.alarmMinutes}")

                        state.copy(
                            alarmTime = alarmLocalTime.toString()
                        )
                    }
                    if (id != -1) {
                        _event.send(AlarmSettingEvent.OnAlarmSuccessCreateOrUpdate(id, _state.value.alarmTime))
                    }
                }
            }

            is AlarmSettingsAction.OnAlarmHoursChange -> {
                _state.update {
                    it.copy(
                        alarmHours = when {
                            action.hours.length > 2 -> it.alarmHours
                            (action.hours.toIntOrNull() ?: 0) > 23 -> "23"
                            else -> action.hours
                        }
                    )
                }
            }

            is AlarmSettingsAction.OnAlarmMinutesChange -> {
                _state.update {
                    it.copy(
                        alarmMinutes = when {
                            action.minutes.length > 2 -> it.alarmHours
                            (action.minutes.toIntOrNull() ?: 0) > 59 -> "59"
                            else -> action.minutes
                        }
                    )
                }
            }

            is AlarmSettingsAction.OnAlarmNameChange -> {
                _state.update {
                    it.copy(
                        alarmName = action.alarmNewName
                    )
                }
            }

            is AlarmSettingsAction.OnRepeatClick -> {
                _state.update {
                    it.copy(
                        selectedDays = it.selectedDays.map { pair ->
                            if (pair.first == action.selectedDay) {
                                pair.copy(second = !pair.second)
                            } else {
                                pair
                            }
                        }
                    )
                }
            }

            is AlarmSettingsAction.OnAvailableRingtoneChange -> {
                _state.update {
                    it.copy(
                        availableRingtone = it.availableRingtone + Pair(action.title, action.uri),
                        alarmRingtone = (if (action.title.contains(it.alarmRingtone)) action.title else it.alarmRingtone).toString(),
                        alarmRingtoneUri = (if (action.title.contains(it.alarmRingtone)) action.uri else it.alarmRingtoneUri).toString(),
                    )
                }
            }

            is AlarmSettingsAction.OnDoneLoadAllRingtones -> {
                _state.update {
                    it.copy(
                        isLoadingRingtones = action.value
                    )
                }
            }

            is AlarmSettingsAction.OnAlarmRingtoneChange -> {
                _state.update {
                    it.copy(
                        alarmRingtone = action.title,
                        alarmRingtoneUri = action.uri.toString()
                    )
                }
            }

            is AlarmSettingsAction.OnAlarmVolumeChange -> {
                _state.update {
                    it.copy(
                        alarmVolume = action.volume
                    )
                }
            }

            is AlarmSettingsAction.OnVibrateChange -> {
                _state.update {
                    it.copy(
                        isVibrate = action.value
                    )
                }
            }

            else -> {}
        }
    }

    private fun String.isValidHour() = length == 2 && toIntOrNull() in 0..23
    private fun String.isValidMinute() = length == 2 && toIntOrNull() in 0..59
}