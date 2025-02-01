package id.dev.snoozeloo.alarm.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.snoozeloo.R
import id.dev.snoozeloo.alarm.presentation.list.component.CardAlarmList
import id.dev.snoozeloo.alarm.presentation.model.AlarmState
import id.dev.snoozeloo.core.presentation.ui.theme.SnoozelooTheme
import id.dev.snoozeloo.core.presentation.ui.theme.backgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.gradientBackground
import id.dev.snoozeloo.core.presentation.ui.theme.montserrat
import org.koin.androidx.compose.koinViewModel
import java.time.Duration

@Composable
fun AlarmListScreenRoot(
    onAlarmClick: (Int?) -> Unit,
    viewModel: AlarmListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AlarmListScreen(
        state = state,
        onAction = {
            when (it) {
                is AlarmListAction.OnAlarmClick -> {
                    onAlarmClick(it.id)
                }

                else -> {}
            }
            viewModel.onAction(it)
        },
    )
}

@Composable
private fun AlarmListScreen(
    state: AlarmListState,
    onAction: (AlarmListAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 12.dp, end = 12.dp, top = 12.dp)
    ) {
        Text(
            "Your Alarms",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = montserrat
        )
        if (state.alarms.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        "It's empty! Add the first alarm so you don't miss an important moment!",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = montserrat,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.alarms, key = { it.id ?: 0 }) {
                    CardAlarmList(
                        alarmState = it,
                        modifier = Modifier

                            .clickable {
                                onAction(AlarmListAction.OnAlarmClick(it.id))
                            },
                        onAction = onAction
                    )
                }
            }

        }
    }
}

@Preview
@Composable
private fun AlarmListScreenPreview() {
    SnoozelooTheme {
        AlarmListScreen(
            state = AlarmListState(
                alarms = listOf(
                    AlarmState(
                        id = 1,
                        alarmName = "Wake up",
                        isActive = false,
                        alarmTime = "12:15 PM",
                        alarmForTime = Duration.ofHours(3),
                        timeToBed = "08:55 AM",
                    ),
                    AlarmState(
                        id = 2,
                        alarmName = "Work",
                        isActive = true,
                        alarmTime = "22:15 PM",
                        alarmForTime = Duration.ofHours(5),
                        timeToBed = "08:55 PM",
                    ),
                    AlarmState(
                        id = 3,
                        alarmName = "Eat Code Repeat",
                        isActive = true,
                        alarmTime = "03:15 PM",
                        alarmForTime = Duration.ofHours(6),
                        timeToBed = "01:55 PM",
                    ),
                    AlarmState(
                        id = 4,
                        alarmName = "Eat Code Repeat",
                        isActive = true,
                        alarmTime = "03:15 PM",
                        alarmForTime = Duration.ofHours(4),
                        timeToBed = "01:55 PM",
                    )
                )
            ),
            onAction = {}
        )
    }
}