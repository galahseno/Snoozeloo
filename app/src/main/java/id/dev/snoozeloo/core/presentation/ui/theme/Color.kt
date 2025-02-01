package id.dev.snoozeloo.core.presentation.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val backgroundColor = Color(0xFFF6F6F6)
val cardBackgroundColor = Color(0xFFFFFFFF)
val switchCheckedBackgroundColor = Color(0xFFFE8B0D)
val switchUncheckedBackgroundColor = Color(0xFFFFDEBB)

val gradientBackground = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFBD01),
        Color(0xFFFD790F),
    ),
    start = Offset(0.0f, 0.5f),
)

val customTextSelectionColors = TextSelectionColors(
    handleColor = switchCheckedBackgroundColor,
    backgroundColor = switchUncheckedBackgroundColor
)