package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.InternalFoundationTextApi
import androidx.compose.foundation.text.TextDelegate
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontLoader
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp


@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    color: Color = Color.Unspecified,
    suggestedFontSizes: List<TextUnit> = listOf(18.sp,16.sp,14.sp,12.sp,10.sp,8.sp,6.sp,4.sp),
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign = TextAlign.Center,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    AutoSizeText(
        AnnotatedString(text),
        modifier,
        color,
        suggestedFontSizes,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        emptyMap(),
        onTextLayout,
        style,
    )
}

@Composable
fun AutoSizeText(
    text: AnnotatedString,
    modifier: Modifier = Modifier.fillMaxWidth(),
    color: Color = Color.Unspecified,
    suggestedFontSizes: List<TextUnit> = listOf(18.sp,16.sp,14.sp,12.sp,10.sp,8.sp,6.sp,4.sp),
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign = TextAlign.Center,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        var combinedTextStyle = (LocalTextStyle.current + style).copy(
            fontSize = min(maxWidth, maxHeight).value.sp
        )

        val fontSizes = suggestedFontSizes.ifEmpty {
            MutableList(combinedTextStyle.fontSize.value.toInt()) {
                (combinedTextStyle.fontSize.value - it).sp
            }
        }

        var currentFontIndex = 0

        combinedTextStyle =
            combinedTextStyle.copy(fontSize = 18.sp)

        while (shouldShrink(text, combinedTextStyle, maxLines) && currentFontIndex < fontSizes.size - 1) {
            combinedTextStyle =
                combinedTextStyle.copy(fontSize = fontSizes[++currentFontIndex])
        }

        Text(
            text,
            Modifier,
            color,
            TextUnit.Unspecified,
            fontStyle,
            fontWeight,
            fontFamily,
            letterSpacing,
            textDecoration,
            textAlign,
            lineHeight,
            overflow,
            softWrap,
            maxLines,
            inlineContent,
            onTextLayout,
            combinedTextStyle,
        )
    }
}

@OptIn(InternalFoundationTextApi::class)
@Composable
private fun BoxWithConstraintsScope.shouldShrink(
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int
): Boolean {
    val textDelegate = TextDelegate(
        text,
        textStyle,
        maxLines,
        true,
        TextOverflow.Clip,
        LocalDensity.current,
        LocalFontLoader.current,
    )

    val textLayoutResult = textDelegate.layout(
        constraints,
        LocalLayoutDirection.current,
    )

    return textLayoutResult.hasVisualOverflow
}