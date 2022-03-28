package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset

@Composable
fun InputEditText(
    value: String,
    modifier: Modifier,
    onValueChange: (String) -> Unit,
    contentTextStyle: TextStyle,
    hintTextStyle: TextStyle,
    placeHolderString: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    cursorColor: Color = Color.Black,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = contentTextStyle,
        decorationBox = {innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        if (contentTextStyle.textAlign == TextAlign.Start)
                            IntOffset(x = 10, y = 0)
                        else
                            IntOffset(x = 0, y = 0)
                    },
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeHolderString,
                        color = hintTextStyle.color,
                        fontSize = hintTextStyle.fontSize
                    )
                }

                innerTextField()

            }
        },
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        cursorBrush = SolidColor(cursorColor)
    )
}
