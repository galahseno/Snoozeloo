package id.dev.snoozeloo.alarm.presentation.trigger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.snoozeloo.alarm.domain.AlarmRepository
import id.dev.snoozeloo.alarm.presentation.toAlarmModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlarmTriggerViewModel(
    private val id: Int,
    private val alarmRepository: AlarmRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AlarmTriggerState())
    val state = _state
        .onStart {
            if (id != -1) {
                val alarmSettings = alarmRepository.getAlarmById(id)
                _state.update {
                    it.copy(
                        id = alarmSettings.id,
                        alarmTime = alarmSettings.alarmTime,
                        alarmName = alarmSettings.alarmName,
                        snoozedTime = alarmSettings.snoozedTime,
                        isActive = alarmSettings.isActive,
                        alarmRingtone = alarmSettings.alarmRingtone,
                        alarmRingtoneUri = alarmSettings.alarmRingtoneUri,
                        alarmVolume = alarmSettings.alarmVolume,
                        isVibrate = alarmSettings.isVibrate,
                        selectedDays = alarmSettings.selectedDays,
                    )
                }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            AlarmTriggerState()
        )

    fun onAction(action: AlarmTriggerAction) {
        when (action) {
            is AlarmTriggerAction.OnTurnOffClicked -> {
                _state.update {
                    it.copy(isSnoozed = false)
                }
            }

            is AlarmTriggerAction.OnSnoozeClicked -> {
                _state.update {
                    it.copy(isSnoozed = true)
                }
            }
        }
    }

    fun updateAlarmSettingWhenSnooze(snoozedTime: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    snoozedTime = snoozedTime
                )
            }
            alarmRepository.modifyAlarmSettings(_state.value.toAlarmModel())
        }
    }
}