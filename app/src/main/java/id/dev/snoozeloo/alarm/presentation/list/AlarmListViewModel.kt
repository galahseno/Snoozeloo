package id.dev.snoozeloo.alarm.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.snoozeloo.alarm.domain.AlarmRepository
import id.dev.snoozeloo.alarm.presentation.toAlarmListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlarmListViewModel(
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AlarmListState())
    val state = _state.asStateFlow()

    init {
        alarmRepository
            .getAlarms()
            .onEach { list ->
                _state.update {
                    it.copy(
                        alarms = list.map { listAlarm ->
                            listAlarm.toAlarmListState()
                        }
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun onAction(action: AlarmListAction) {
        when (action) {
            is AlarmListAction.OnActiveAlarmChange -> {
                viewModelScope.launch {
                    action.id?.let {
                        alarmRepository.changeAlarmStatus(it, action.value)
                    }
                }
            }

            else -> {}
        }
    }
}