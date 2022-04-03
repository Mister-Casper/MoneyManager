package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.util.Date

@Composable
fun SelectTimeIntervalDialog(
    onDismiss: () -> Unit,
    defaultStart: Date,
    defaultEnd: Date,
    onResult: (startDate: Date, endDate: Date) -> Unit,
    isDark: Boolean
) {
    var isShowDialog by remember { mutableStateOf(false) }
    var isFirst by remember { mutableStateOf(true) }

    var firstDate by remember { mutableStateOf(defaultStart) }
    var secondDate by remember { mutableStateOf(defaultEnd) }

    if (isShowDialog) {
        val defaultDate = if (isFirst) firstDate else secondDate
        DatePicker(
            defaultDate = defaultDate,
            onDateSelected = {
                if (isFirst) firstDate = Date(it) else secondDate = Date(it)
                if (secondDate.epochMillis < firstDate.epochMillis) {
                    secondDate = firstDate
                }
            },
            onDismissRequest = {
                isShowDialog = false
            }, isDark
        )
    }

    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(id = R.string.select_date))
        },
        text = {
            Column(Modifier.padding(top = 8.dp)) {
                Text(text = stringResource(id = R.string.start))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clickable {
                            isFirst = true
                            isShowDialog = true
                        }) {
                    Text(
                        text = firstDate.toDayMonthString(),
                        Modifier.weight(1f),
                        fontWeight = FontWeight.Thin
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                Divider()
                Text(
                    text = stringResource(id = R.string.end),
                    Modifier.padding(top = 8.dp)
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clickable {
                            isFirst = false
                            isShowDialog = true
                        }) {
                    Text(
                        text = secondDate.toDayMonthString(),
                        Modifier.weight(1f),
                        fontWeight = FontWeight.Thin
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                Divider()
            }
        },
        confirmButton = {
            Button(onClick = {
                onResult(firstDate, secondDate)
            }) {
                Text(text = stringResource(id = R.string.done), color = white)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel), color = white)
            }
        })

}