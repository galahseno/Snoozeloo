package id.dev.snoozeloo.alarm.presentation.trigger

import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.snoozeloo.R
import id.dev.snoozeloo.alarm.presentation.parse24HourToLocalTime
import id.dev.snoozeloo.core.presentation.AudioPlay
import id.dev.snoozeloo.core.presentation.alarm.AlarmService
import id.dev.snoozeloo.core.presentation.alarm.AlarmService.Companion.EXTRA_ID
import id.dev.snoozeloo.core.presentation.ui.theme.SnoozelooTheme
import id.dev.snoozeloo.core.presentation.ui.theme.gradientBackground
import id.dev.snoozeloo.core.presentation.ui.theme.montserrat
import id.dev.snoozeloo.core.presentation.ui.theme.switchCheckedBackgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.switchUncheckedBackgroundColor
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.time.ZonedDateTime


class AlarmTriggerActivity : ComponentActivity() {
    private val viewModel by inject<AlarmTriggerViewModel>(
        parameters = { parametersOf(intent.getIntExtra(EXTRA_ID, -1)) }
    )

    private val notificationManager by lazy {
        getSystemService<NotificationManager>() as NotificationManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            })

        setContent {
            SnoozelooTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()

                AlarmTriggerScreen(
                    state = state,
                    onAction = { action ->
                        viewModel.onAction(action)

                        when (action) {
                            is AlarmTriggerAction.OnTurnOffClicked -> {
                                state.id?.let { id ->
                                    notificationManager.cancel(id)
                                    this.startService(
                                        AlarmService.createStopIntent(
                                            context = this
                                        )
                                    )
                                    AudioPlay.pauseAudio()
                                    viewModel.updateAlarmSettingWhenSnooze("")

                                    finish()
                                }
                            }

                            is AlarmTriggerAction.OnSnoozeClicked -> {
                                state.id?.let { id ->
                                    notificationManager.cancel(id)
                                    this.startService(
                                        AlarmService.createStopIntent(
                                            context = this
                                        )
                                    )
                                    AudioPlay.pauseAudio()

                                    val now = ZonedDateTime.now()
                                    val alarmLocalTime =
                                        parse24HourToLocalTime(state.snoozedTime.ifEmpty {
                                            state.alarmTime
                                        })
                                    val alarmDateTime = now.with(alarmLocalTime).plusMinutes(5)
                                    val nextAlarmTime = alarmDateTime
                                        .toLocalTime().toString()

                                    viewModel.updateAlarmSettingWhenSnooze(nextAlarmTime)
                                    AlarmService().setDailyReminder(
                                        this@AlarmTriggerActivity,
                                        id,
                                        nextAlarmTime
                                    )
                                    finish()
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AlarmTriggerScreen(
    state: AlarmTriggerState,
    onAction: (AlarmTriggerAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val alarmTime by remember(state.alarmTime) {
        mutableStateOf(if (state.isSnoozed) state.snoozedTime else state.alarmTime)
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            10.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.alarm),
            contentDescription = "alarm icon",
            modifier = Modifier
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            gradientBackground,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                }
        )
        Text(
            text = alarmTime,
            fontFamily = montserrat,
            fontWeight = FontWeight.Medium,
            fontSize = 82.sp,
            color = switchCheckedBackgroundColor
        )
        if (state.alarmName.isNotEmpty()) {
            Text(
                text = state.alarmName,
                fontFamily = montserrat,
                fontWeight = FontWeight.W600,
                fontSize = 24.sp,
                color = switchCheckedBackgroundColor
            )
        }
        Button(
            modifier = Modifier.width(270.dp),
            onClick = {
                onAction(AlarmTriggerAction.OnTurnOffClicked)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = switchCheckedBackgroundColor,
            )
        ) {
            Text(
                "Turn Off",
                fontFamily = montserrat,
                fontWeight = FontWeight.W600,
                fontSize = 24.sp
            )
        }
        OutlinedButton(
            modifier = Modifier.width(270.dp),
            border = BorderStroke(1.dp, switchCheckedBackgroundColor),
            onClick = {
                onAction(AlarmTriggerAction.OnSnoozeClicked)
            },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = switchUncheckedBackgroundColor,
                contentColor = switchCheckedBackgroundColor
            )
        ) {
            Text(
                "Snooze for 5 min",
                fontFamily = montserrat,
                fontWeight = FontWeight.W600,
                fontSize = 24.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AlarmTriggerScreenPreview() {
    SnoozelooTheme {
        AlarmTriggerScreen(
            state = AlarmTriggerState(
                alarmTime = "10:15",
                alarmName = "Woey"
            ),
            onAction = {}
        )
    }
}