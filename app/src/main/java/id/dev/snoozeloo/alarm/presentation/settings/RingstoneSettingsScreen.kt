package id.dev.snoozeloo.alarm.presentation.settings

import android.media.RingtoneManager
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.snoozeloo.R
import id.dev.snoozeloo.core.presentation.AudioPlay
import id.dev.snoozeloo.core.presentation.ui.theme.SnoozelooTheme
import id.dev.snoozeloo.core.presentation.ui.theme.backgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.cardBackgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.montserrat
import id.dev.snoozeloo.core.presentation.ui.theme.switchCheckedBackgroundColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@Composable
fun RingtoneSettingsScreenRoot(
    onBackClick: () -> Unit,
    viewModel: AlarmSettingsViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            AudioPlay.pauseAudio()
        }
    }

    LaunchedEffect(true) {
        if (state.availableRingtone.size == 2) {
            withContext(Dispatchers.IO) {
                val manager = RingtoneManager(context)
                manager.setType(RingtoneManager.TYPE_RINGTONE)
                val cursor = manager.cursor
                while (cursor.moveToNext()) {
                    val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                    val ringtoneURI = manager.getRingtoneUri(cursor.position)

                    viewModel.onAction(
                        AlarmSettingsAction.OnAvailableRingtoneChange(
                            title,
                            ringtoneURI
                        )
                    )
                }
                cursor.close()
                delay(500)
                viewModel.onAction(AlarmSettingsAction.OnDoneLoadAllRingtones(false))
            }
        }
    }

    RingtoneSettingsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is AlarmSettingsAction.OnCloseRingtoneClick -> onBackClick()
                is AlarmSettingsAction.OnAlarmRingtoneChange -> {
                    if (action.uri.toString() != "Silent") {
                        AudioPlay.playAudio(context, action.uri, false)
                    }
                }

                else -> {}
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun RingtoneSettingsScreen(
    state: AlarmSettingState,
    onAction: (AlarmSettingsAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(switchCheckedBackgroundColor)
                .padding(3.dp)
                .clickable {
                    onAction(AlarmSettingsAction.OnCloseRingtoneClick)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "close",
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }
        if (state.isLoadingRingtones) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = switchCheckedBackgroundColor
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.availableRingtone, key = { it.first }) {
                    ListItem(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(cardBackgroundColor)
                            .clickable {
                                onAction(
                                    AlarmSettingsAction.OnAlarmRingtoneChange(
                                        it.first,
                                        it.second
                                    )
                                )
                            },
                        leadingContent = {
                            Image(
                                imageVector = ImageVector.vectorResource(
                                    if (it.second.toString() == "Silent") R.drawable.silent_icon
                                    else R.drawable.ringtone_icon
                                ),
                                contentDescription = "silent",
                            )
                        },
                        headlineContent = {
                            Text(
                                text = it.first,
                                fontFamily = montserrat,
                                fontWeight = FontWeight.W600
                            )
                        },
                        trailingContent = {
                            if (it.first == state.alarmRingtone) {
                                Image(
                                    imageVector = ImageVector.vectorResource(R.drawable.selected_ringtone),
                                    contentDescription = "selected",
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RingtoneSettingsScreenPreview() {
    SnoozelooTheme {
        RingtoneSettingsScreen(
            state = AlarmSettingState(
                availableRingtone = listOf(
                    Pair("Silent", Uri.parse("Silent")),
                    Pair("Default (Bright Morning)", Uri.parse("Default")),
                    Pair("Bright Morning", Uri.parse("Bright Morning")),
                    Pair("Cuckoo Clock", Uri.parse("Cuckoo Clock")),
                    Pair("Early Twilight", Uri.parse("Early Twilight")),
                ),
                alarmRingtone = "Default (Bright Morning)",
                isLoadingRingtones = false
            ),
            onAction = {}
        )
    }
}