package id.dev.snoozeloo.alarm.presentation.settings

import android.media.RingtoneManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.snoozeloo.alarm.presentation.chunkedSelectedAndNotSelectedDays
import id.dev.snoozeloo.alarm.presentation.settings.component.ActionMenu
import id.dev.snoozeloo.alarm.presentation.settings.component.AlarmNameDialog
import id.dev.snoozeloo.alarm.presentation.settings.component.CardAdvanceSettings
import id.dev.snoozeloo.alarm.presentation.settings.component.CardSimpleSettings
import id.dev.snoozeloo.alarm.presentation.settings.component.CardTimeAlarm
import id.dev.snoozeloo.core.presentation.alarm.AlarmService
import id.dev.snoozeloo.core.presentation.ui.ObserveAsEvents
import id.dev.snoozeloo.core.presentation.ui.theme.SnoozelooTheme
import id.dev.snoozeloo.core.presentation.ui.theme.backgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.gradientBackground
import id.dev.snoozeloo.core.presentation.ui.theme.montserrat
import id.dev.snoozeloo.core.presentation.ui.theme.switchCheckedBackgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.switchUncheckedBackgroundColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AlarSettingsScreenRoot(
    onBackClick: () -> Unit,
    onRingtoneClick: () -> Unit,
    onSuccessCreateOrUpdateAlarm: () -> Unit,
    viewModel: AlarmSettingsViewModel
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var dialogAlarmNameVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(true) {
        withContext(Dispatchers.IO) {
            if (state.alarmRingtoneUri.isEmpty()) {
                val defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                    context,
                    RingtoneManager.TYPE_RINGTONE
                )
                val defaultRingtone = RingtoneManager.getRingtone(context, defaultRingtoneUri)

                viewModel.onAction(
                    AlarmSettingsAction.OnAvailableRingtoneChange(
                        "Default (${defaultRingtone.getTitle(context)})",
                        defaultRingtoneUri
                    )
                )
            }
        }
    }

    ObserveAsEvents(
        viewModel.event,
        onEvent = { event ->
            when (event) {
                is AlarmSettingEvent.OnAlarmSuccessCreateOrUpdate -> {
                    AlarmService().setDailyReminder(
                        context,
                        event.id,
                        event.alarmTime
                    )
                    onSuccessCreateOrUpdateAlarm()
                }
            }
        }
    )
    if (dialogAlarmNameVisible) {
        AlarmNameDialog(
            alarmName = state.alarmName,
            onDismissRequest = { dialogAlarmNameVisible = false },
            onSave = {
                viewModel.onAction(AlarmSettingsAction.OnAlarmNameChange(it))
                dialogAlarmNameVisible = false
            })
    }

    AlarSettingsScreenRootScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is AlarmSettingsAction.OnAlarmNameClick -> {
                    dialogAlarmNameVisible = !dialogAlarmNameVisible
                }

                is AlarmSettingsAction.OnCloseAlarmClick -> onBackClick()
                is AlarmSettingsAction.OnAlarmRingtoneClick -> onRingtoneClick()
                else -> {}
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun AlarSettingsScreenRootScreen(
    state: AlarmSettingState,
    onAction: (AlarmSettingsAction) -> Unit
) {
    val chunkedList by remember(state.selectedDays) {
        mutableStateOf(chunkedSelectedAndNotSelectedDays(state.selectedDays))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ActionMenu(
            isSavedEnable = state.isSavedEnable,
            onAction = onAction
        )
        CardTimeAlarm(
            state = state,
            onAction = onAction
        )
        CardSimpleSettings(
            cardName = "Alarm Name",
            cardContent = {
                Text(
                    state.alarmName,
                    fontFamily = montserrat,
                )
            },
            modifier = Modifier
                .clickable {
                    onAction(AlarmSettingsAction.OnAlarmNameClick)
                }
        )
        CardAdvanceSettings(
            cardName = "Repeat",
            cardContent = {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth(),
                ) {
                    chunkedList.forEach { listSelectedDays ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (listSelectedDays.all { it.second }) {
                                        Modifier
                                            .clip(RoundedCornerShape(60))
                                            .background(gradientBackground)
                                    } else Modifier
                                ),
                        ) {
                            listSelectedDays.forEach { listSelectedDay ->
                                Surface(
                                    modifier = Modifier
                                        .padding(horizontal = 2.dp)
                                        .height(SuggestionChipDefaults.Height)
                                        .clip(RoundedCornerShape(60))
                                        .then(
                                            if (!listSelectedDay.second)
                                                Modifier.background(
                                                    switchUncheckedBackgroundColor.copy(
                                                        alpha = 0.3f
                                                    )
                                                )
                                            else Modifier
                                        )
                                        .clickable {
                                            onAction(
                                                AlarmSettingsAction.OnRepeatClick(
                                                    listSelectedDay.first
                                                )
                                            )
                                        }
                                        .padding(vertical = 8.dp, horizontal = 16.dp),
                                    color = Color.Transparent
                                ) {
                                    Text(
                                        listSelectedDay.first.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.W500,
                                        fontFamily = montserrat,
                                        color = if (listSelectedDay.second) Color.White
                                        else switchCheckedBackgroundColor.copy(alpha = 0.3f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
        CardSimpleSettings(
            cardName = "Alarm ringtone",
            cardContent = {
                Text(
                    state.alarmRingtone,
                    fontFamily = montserrat,
                )
            },
            modifier = Modifier
                .clickable {
                    onAction(AlarmSettingsAction.OnAlarmRingtoneClick)
                }
        )
        CardAdvanceSettings(
            cardName = "Alarm Volume",
            cardContent = {
                Slider(
                    value = state.alarmVolume,
                    onValueChange = {
                        onAction(AlarmSettingsAction.OnAlarmVolumeChange(it))
                    },
                    colors = SliderDefaults.colors(
                        activeTrackColor = switchCheckedBackgroundColor,
                        thumbColor = switchCheckedBackgroundColor,
                        inactiveTrackColor = switchUncheckedBackgroundColor
                    )
                )
            }
        )
        CardSimpleSettings(
            cardName = "Vibrate",
            cardContent = {
                Switch(
                    checked = state.isVibrate,
                    onCheckedChange = {
                        onAction(AlarmSettingsAction.OnVibrateChange(it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = switchCheckedBackgroundColor,
                        uncheckedTrackColor = switchUncheckedBackgroundColor,
                        uncheckedBorderColor = Color.Transparent,
                        uncheckedThumbColor = Color.White,
                    )
                )
            }
        )
    }
}

@Preview
@Composable
private fun AlarSettingsScreenRootScreenPreview() {
    SnoozelooTheme {
        AlarSettingsScreenRootScreen(
            state = AlarmSettingState(
                alarmHours = "02",
                isSavedEnable = true,
                isVibrate = true
            ),
            onAction = {}
        )
    }
}