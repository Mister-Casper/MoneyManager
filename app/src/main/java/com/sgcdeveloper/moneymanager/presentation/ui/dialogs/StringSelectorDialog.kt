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

@Composable
fun StringSelectorDialog(
    title:String,
    items:List<String>,
    defaultValue: Any,
    onSelected: (day: Any) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(items.size) {
                    val day = items[it]
                    Box(modifier = Modifier
                        .clickable {
                            onSelected(day)
                            onDismiss()
                        }
                        .fillMaxWidth()) {
                        Row(Modifier.fillMaxHeight()) {
                            RadioButton(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                selected = (day == defaultValue),
                                onClick = {}
                            )
                            Text(
                                text = day,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .align(Alignment.CenterVertically)
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