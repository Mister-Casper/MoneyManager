package com.sgcdeveloper.moneymanager.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = blue,
    secondary = white,
    surface = dark_gray2,
    background = black,
    onPrimary = white,
    onSecondary = white,
    onBackground = white,
    onSurface = white,
)

private val LightColorPalette = lightColors(
    primary = blue,
    primaryVariant = black,
    secondary = black,
    surface = light_gray,
    onBackground = black,
    background = white
)

@Composable
fun MoneyManagerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}