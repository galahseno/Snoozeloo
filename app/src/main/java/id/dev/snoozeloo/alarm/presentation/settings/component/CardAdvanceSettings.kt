package id.dev.snoozeloo.alarm.presentation.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.dev.snoozeloo.core.presentation.ui.theme.SnoozelooTheme
import id.dev.snoozeloo.core.presentation.ui.theme.cardBackgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.montserrat

@Composable
fun CardAdvanceSettings(
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun CardAdvanceSettingsPreview() {
    SnoozelooTheme {
        CardAdvanceSettings(
            cardName = "Alarm Volume",
            cardContent = {
                Slider(
                    state = SliderState(value = 0.5f)
                )
            }
        )
    }
}