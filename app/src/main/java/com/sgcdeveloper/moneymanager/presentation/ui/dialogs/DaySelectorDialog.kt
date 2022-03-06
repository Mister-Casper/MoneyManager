package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.white
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@Composable
fun DaySelectorDialog(
    defaultDay: DayOfWeek,
    onSelected: (day: DayOfWeek) -> Unit,
    onDismiss: () -> Unit
) {
    val days = DayOfWeek.values()

    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.first_day), color = MaterialTheme.colors.secondary)
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(days.size) {
                    val day = days[it]
                    Box(modifier = Modifier
                        .clickable {
                            onSelected(day)
                            onDismiss()
                        }
                        .fillMaxWidth()) {
                        Row(Modifier.fillMaxHeight()) {
                            RadioButton(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                selected = (day == defaultDay),
                                onClick = {}
                            )
                            Text(
                                text = day.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .align(Alignment.CenterVertically),
                                color = MaterialTheme.colors.secondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel), color = white)
            }
        })
}