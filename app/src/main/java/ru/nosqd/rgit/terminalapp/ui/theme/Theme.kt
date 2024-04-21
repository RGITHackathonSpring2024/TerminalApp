package ru.nosqd.rgit.terminalapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.core.view.WindowCompat

val AppColorScheme = darkColorScheme(
    primary = Color(0xFF32AE52),
    secondary = Color(0xFFE65100),
    tertiary = Color(0xFF004D40),
    background = Color(0xFF2C2C2C),
    surface = Color(0xFF181818)
)

@Composable
fun TerminalAppTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppColorScheme.primary.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = {
            ProvideTextStyle(
                value = TextStyle(color = Color.White),
                content = { Surface(color = AppColorScheme.surface, content = content, modifier = Modifier.fillMaxSize()) }
            )
        }
    )
}