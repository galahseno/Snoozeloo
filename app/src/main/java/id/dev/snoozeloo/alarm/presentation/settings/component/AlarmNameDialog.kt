package id.dev.snoozeloo.alarm.presentation.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.dev.snoozeloo.core.presentation.ui.theme.cardBackgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.customTextSelectionColors
import id.dev.snoozeloo.core.presentation.ui.theme.montserrat
import id.dev.snoozeloo.core.presentation.ui.theme.switchCheckedBackgroundColor
import id.dev.snoozeloo.core.presentation.ui.theme.switchUncheckedBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmNameDialog(
    alarmName: String,
    onDismissRequest: () -> Unit,
    onSave: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var alarmNewName by rememberSaveable { mutableStateOf(alarmName) }

    BasicAlertDialog(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(cardBackgroundColor)
            .padding(16.dp),
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "Alarm Name",
                fontFamily = montserrat,
                fontWeight = FontWeight.W600
            )
            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                OutlinedTextField(
                    value = alarmNewName,
                    onValueChange = {
                        alarmNewName = it
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        cursorColor = switchCheckedBackgroundColor,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = switchUncheckedBackgroundColor,
                        unfocusedIndicatorColor = switchUncheckedBackgroundColor,
                    ),
                    textStyle = TextStyle(
                        fontFamily = montserrat
                    )
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { onSave(alarmNewName) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = switchCheckedBackgroundColor,
                    )
                ) {
                    Text(
                        "Save",
                        fontFamily = montserrat,
                    )
                }
            }
        }
    }
}