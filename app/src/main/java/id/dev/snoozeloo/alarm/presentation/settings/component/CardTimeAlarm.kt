package id.dev.snoozeloo.alarm.presentation.settings.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import id.dev.snoozeloo.alarm.presentation.settings.AlarmSettingState
import id.dev.snoozeloo.alarm.presentation.settings.AlarmSettingsAction
import id.dev.snoozeloo.core.presentation.ui.theme.SnoozelooTheme
import id.dev.snoozeloo.core.presentation.ui.theme.backgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.cardBackgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.customTextSelectionColors
import id.dev.snoozeloo.core.presentation.ui.theme.gradientBackground
import id.dev.snoozeloo.core.presentation.ui.theme.montserrat
import id.dev.snoozeloo.core.presentation.ui.theme.switchCheckedBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardTimeAlarm(
    state: AlarmSettingState,
    onAction: (AlarmSettingsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSourceHours = remember { MutableInteractionSource() }
    val interactionSourceMinutes = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val hourseFocusRequest = FocusRequester()
    val minutesFocusRequest = FocusRequester()

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor,
            disabledContentColor = cardBackgroundColor
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    BasicTextField(
                        interactionSource = interactionSourceHours,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        value = state.alarmHours,
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                onAction(AlarmSettingsAction.OnAlarmHoursChange(it))
                                if (it.length >= 2) {
                                    minutesFocusRequest.requestFocus()
                                } else if (it.isEmpty()) {
                                    keyboardController?.hide()
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .size(128.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(backgroundColor)
                            .focusRequester(hourseFocusRequest),
                        cursorBrush = gradientBackground,
                        textStyle = TextStyle(
                            fontFamily = montserrat,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            fontSize = 52.sp,
                            color = if (state.alarmHours.isNotEmpty()) {
                                switchCheckedBackgroundColor
                            } else {
                                Color.Gray
                            }
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                OutlinedTextFieldDefaults.DecorationBox(
                                    value = state.alarmHours,
                                    innerTextField = innerTextField,
                                    enabled = true,
                                    singleLine = true,
                                    visualTransformation = VisualTransformation.None,
                                    interactionSource = interactionSourceHours,
                                    placeholder = {
                                        Text(
                                            "00",
                                            style = TextStyle(
                                                fontFamily = montserrat,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 52.sp,
                                                color = Color.Gray
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(),
                                    contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                                    container = {

                                    },
                                )
                            }
                        }
                    )
                }
                Text(
                    ":",
                    fontFamily = montserrat,
                    style = MaterialTheme.typography.displayLarge
                )
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    BasicTextField(
                        interactionSource = interactionSourceMinutes,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        value = state.alarmMinutes,
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                onAction(AlarmSettingsAction.OnAlarmMinutesChange(it))
                                if (it.length >= 2) {
                                    keyboardController?.hide()
                                } else if (it.isEmpty()) {
                                    hourseFocusRequest.requestFocus()
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .size(128.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(backgroundColor)
                            .focusRequester(minutesFocusRequest),
                        cursorBrush = gradientBackground,
                        textStyle = TextStyle(
                            fontFamily = montserrat,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            fontSize = 52.sp,
                            color = if (state.alarmMinutes.isNotEmpty()) {
                                switchCheckedBackgroundColor
                            } else {
                                Color.Gray
                            }
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                OutlinedTextFieldDefaults.DecorationBox(
                                    value = state.alarmMinutes,
                                    innerTextField = innerTextField,
                                    enabled = true,
                                    singleLine = true,
                                    visualTransformation = VisualTransformation.None,
                                    interactionSource = interactionSourceMinutes,
                                    placeholder = {
                                        Text(
                                            "00",
                                            style = TextStyle(
                                                fontFamily = montserrat,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 52.sp,
                                                color = Color.Gray
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(),
                                    contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                                    container = {

                                    },
                                )
                            }
                        }
                    )
                }
            }
            AnimatedVisibility(
                visible = state.alarmInText.isNotEmpty()
            ) {
                Text(
                    "Alarm in ${state.alarmInText}",
                    fontFamily = montserrat,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview
@Composable
private fun CardTimeAlarmPreview() {
    SnoozelooTheme {
        CardTimeAlarm(
            state = AlarmSettingState(
                alarmHours = "02",
                alarmMinutes = "",
                alarmInText = "7h 15min"
            ),
            onAction = {}
        )
    }
}