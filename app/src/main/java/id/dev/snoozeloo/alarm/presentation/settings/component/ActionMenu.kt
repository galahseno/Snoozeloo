package id.dev.snoozeloo.alarm.presentation.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.dev.snoozeloo.alarm.presentation.settings.AlarmSettingsAction
import id.dev.snoozeloo.core.presentation.ui.theme.SnoozelooTheme
import id.dev.snoozeloo.core.presentation.ui.theme.montserrat
import id.dev.snoozeloo.core.presentation.ui.theme.switchCheckedBackgroundColor

@Composable
fun ActionMenu(
    isSavedEnable: Boolean,
    onAction: (AlarmSettingsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(switchCheckedBackgroundColor)
                .padding(3.dp)
                .clickable {
                    onAction(AlarmSettingsAction.OnCloseAlarmClick)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "close",
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }

        Button(
            enabled = isSavedEnable,
            onClick = { onAction(AlarmSettingsAction.OnSaveAlarmClick) },
            colors = ButtonDefaults.buttonColors(
                containerColor = switchCheckedBackgroundColor,
                disabledContentColor = Color.White
            )
        ) {
            Text(
                "Save",
                fontFamily = montserrat,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActionMenuPreview() {
    SnoozelooTheme {
        ActionMenu(
            isSavedEnable = false,
            onAction = {}
        )
    }
}