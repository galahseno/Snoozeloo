package id.dev.snoozeloo.alarm.presentation.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.dev.snoozeloo.core.presentation.ui.theme.SnoozelooTheme
import id.dev.snoozeloo.core.presentation.ui.theme.cardBackgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.montserrat

@Composable
fun CardSimpleSettings(
    cardName: String,
    cardContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor,
            disabledContentColor = cardBackgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                cardName,
                fontFamily = montserrat,
                fontWeight = FontWeight.W600
            )
            cardContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardSimpleSettingsPreview() {
    SnoozelooTheme {
        CardSimpleSettings(
            cardName = "Alarm Name",
            cardContent = {
                Text(
                    "Work",
                    fontFamily = montserrat,
                )
            },
        )
    }
}