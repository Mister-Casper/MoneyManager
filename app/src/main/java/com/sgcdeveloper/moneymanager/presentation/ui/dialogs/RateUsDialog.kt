package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.dark_blue
import com.sgcdeveloper.moneymanager.presentation.theme.white

var ywOffset10 = 0

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RateUsDialog(
    rate: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var selectedStar by remember { mutableStateOf(-1) }
    AlertDialog(
        containerColor = dark_blue,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
        textContentColor = white,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .wrapContentHeight()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (offset.y <= ywOffset10)
                        onDismiss()
                }
            }
            .customDialogModifier10(),
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.get_feedback_title),
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.get_feedback),
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    repeat(5) {
                        val starIcon = if (selectedStar >= it) {
                            painterResource(id = R.drawable.star_active)
                        } else
                            painterResource(id = R.drawable.star)
                        Icon(
                            painter = starIcon,
                            contentDescription = "star",
                            tint = if (selectedStar >= it) Color.Unspecified else white,
                            modifier = Modifier
                                .weight(1f)
                                .size(64.dp)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .clickable {
                                    selectedStar = it
                                }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(0.1f))
                    Button(
                        onClick = { rate() },
                        Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(32.dp),
                    ) {
                        androidx.compose.material.Text(
                            text = stringResource(id = R.string.send_rate),
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.weight(0.1f))
                }
            }
        },
        confirmButton = {}, properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

fun Modifier.customDialogModifier10() = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    ywOffset10 = constraints.maxHeight - placeable.height
    layout(constraints.maxWidth, constraints.maxHeight) {
        placeable.place(0, ywOffset10, 10f)
    }
}