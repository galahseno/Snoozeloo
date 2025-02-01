package id.dev.snoozeloo.alarm.presentation.list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.dev.snoozeloo.alarm.presentation.chunkedSelectedAndNotSelectedDays
import id.dev.snoozeloo.alarm.presentation.formatDuration
import id.dev.snoozeloo.alarm.presentation.list.AlarmListAction
import id.dev.snoozeloo.alarm.presentation.model.AlarmState
import id.dev.snoozeloo.core.presentation.RepeatDayEnum
import id.dev.snoozeloo.core.presentation.alarm.AlarmService
import id.dev.snoozeloo.core.presentation.ui.theme.SnoozelooTheme
import id.dev.snoozeloo.core.presentation.ui.theme.cardBackgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.gradientBackground
import id.dev.snoozeloo.core.presentation.ui.theme.montserrat
import id.dev.snoozeloo.core.presentation.ui.theme.switchCheckedBackgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.switchUncheckedBackgroundColor
import kotlinx.coroutines.delay
import java.time.Duration

@Composable
fun CardAlarmList(
    alarmState: AlarmState,
    onAction: (AlarmListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val chunkedList by remember(alarmState.selectedDays) {
        mutableStateOf(chunkedSelectedAndNotSelectedDays(alarmState.selectedDays))
    }
    var countDownDuration by remember(alarmState.alarmTime) {
        mutableStateOf(alarmState.alarmForTime)
    }

    LaunchedEffect(countDownDuration) {
        while (!countDownDuration.isNegative) {
            delay(1000)
            countDownDuration = countDownDuration.minus(Duration.ofSeconds(1))
        }
        countDownDuration = Duration.ZERO
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor,
            disabledContentColor = cardBackgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    alarmState.alarmName ?: "-",
                    modifier = Modifier
                        .alpha(
                            if (alarmState.isActive) 1f else 0.5f
                        ),
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = montserrat,
                )
                Switch(
                    checked = alarmState.isActive,
                    onCheckedChange = {
                        onAction(AlarmListAction.OnActiveAlarmChange(alarmState.id, it))
                        alarmState.id?.let { _ ->
                            if (it) {
                                AlarmService().setDailyReminder(
                                    context,
                                    alarmState.id,
                                    alarmState.alarmTime
                                )
                            } else {
                                AlarmService().cancelAlarm(context, alarmState.id)
                            }
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = switchCheckedBackgroundColor,
                        uncheckedTrackColor = switchUncheckedBackgroundColor,
                        uncheckedBorderColor = Color.Transparent,
                        uncheckedThumbColor = Color.White,
                    )
                )
            }

            Text(
                alarmState.alarmTime,
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = montserrat,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .alpha(
                        if (alarmState.isActive) 1f else 0.5f
                    ),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Alarm for ${formatDuration(countDownDuration)}",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = montserrat,
                modifier = Modifier
                    .alpha(
                        if (alarmState.isActive) 1f else 0.5f
                    ),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(),
            ) {
                chunkedList.forEach { listSelectedDays ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(
                                if (alarmState.isActive) 1f else 0.5f
                            )
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

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Go to bed at ${alarmState.timeToBed} To get 8h of sleep",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = montserrat,
                modifier = Modifier
                    .alpha(
                        if (alarmState.isActive) 1f else 0.5f
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun CardAlarListPreview() {
    SnoozelooTheme {
        CardAlarmList(
            modifier = Modifier
                .fillMaxWidth()
                .height(225.dp),
            alarmState = AlarmState(
                id = 1,
                alarmName = "Wake up",
                isActive = true,
                alarmTime = "12:15 PM",
                alarmForTime = Duration.ofHours(3),
                timeToBed = "08:22 AM",
                selectedDays = listOf(
                    Pair(RepeatDayEnum.Mo, true),
                    Pair(RepeatDayEnum.Tu, true),
                    Pair(RepeatDayEnum.We, false),
                    Pair(RepeatDayEnum.Th, true),
                    Pair(RepeatDayEnum.Fr, false),
                    Pair(RepeatDayEnum.Sa, true),
                    Pair(RepeatDayEnum.Su, true)
                )
            ),
            onAction = {}
        )
    }
}