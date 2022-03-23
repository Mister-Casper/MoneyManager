package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun RoundedLinearProgressIndicator(
    height: Dp,
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = color.copy(alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity)
) {
    Canvas(
        modifier
            .progressSemantics(progress)
            .fillMaxWidth()
            .height(height)
            .focusable()
    ) {
        val strokeWidth = size.height
        drawRoundedLinearProgressIndicator(
            startFraction = 0f,
            endFraction = 1f,
            color = backgroundColor,
            strokeWidth = strokeWidth
        )
        if(progress != 0.0f) {
            drawRoundedLinearProgressIndicator(
                startFraction = 0f,
                endFraction = progress,
                color = color,
                strokeWidth = strokeWidth
            )
        }
    }
}

private fun DrawScope.drawRoundedLinearProgressIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float,
) {
    val cap = StrokeCap.Round
    val width = size.width
    val height = size.height
    val yOffset = height / 2

    val roundedCapOffset = size.height / 2

    val isLtr = layoutDirection == LayoutDirection.Ltr
    val barStart = (if (isLtr) startFraction else 1f - endFraction) * width + if (isLtr) roundedCapOffset else -roundedCapOffset
    val barEnd = (if (isLtr) endFraction else 1f - startFraction) * width - if (isLtr) roundedCapOffset else -roundedCapOffset

    drawLine(
        color = color,
        start = Offset(barStart, yOffset),
        end = Offset(barEnd, yOffset),
        strokeWidth = strokeWidth,
        cap = cap,
    )
}
