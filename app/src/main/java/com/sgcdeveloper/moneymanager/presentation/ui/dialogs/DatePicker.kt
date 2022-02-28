package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import android.view.ContextThemeWrapper
import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.black
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.util.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DatePicker(
    defaultDate: Date,
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    isDarkTHeme: Boolean = true
) {
    val selDate = remember { mutableStateOf(defaultDate.getAsLocalDate()) }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        modifier = Modifier.size(400.dp, 600.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        text = {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(
                        color = MaterialTheme.colors.surface,
                        shape = RoundedCornerShape(size = 16.dp)
                    )
            ) {
                Column(
                    Modifier
                        .defaultMinSize(minHeight = 72.dp)
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colors.primary,
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Select date".toUpperCase(Locale.ENGLISH),
                        style = MaterialTheme.typography.caption,
                        color = white
                    )

                    Spacer(modifier = Modifier.size(24.dp))

                    Text(
                        text = selDate.value.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                        style = MaterialTheme.typography.h4,
                        color = white
                    )

                    Spacer(modifier = Modifier.size(16.dp))
                }

                CustomCalendarView(defaultDate, onDateSelected = {
                    onDateSelected(it)
                    onDismissRequest()
                },isDarkTHeme)

                Spacer(modifier = Modifier.size(8.dp))

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 16.dp, end = 16.dp)
                ) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            style = MaterialTheme.typography.button,
                            color = white
                        )
                    }
                }
            }
        },
        buttons = {})
}

@Composable
fun CustomCalendarView(defaultDate: Date, onDateSelected: (LocalDate) -> Unit, isDarkTHeme: Boolean) {
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context ->
            if (isDarkTHeme) {
                val view = CalendarView(ContextThemeWrapper(context, R.style.CalenderViewCustom))
                view.setBackgroundColor(black.toArgb())
                view.date = defaultDate.epochMillis
                view
            }else {
                val view = CalendarView(ContextThemeWrapper(context, R.style.CalenderViewCustom_Light))
                view.setBackgroundColor(blue.toArgb())
                view.date = defaultDate.epochMillis
                view
            }
        },
        update = { view ->
            view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                onDateSelected(
                    LocalDate
                        .now()
                        .withMonth(month + 1)
                        .withYear(year)
                        .withDayOfMonth(dayOfMonth)
                )
            }
        }
    )
}