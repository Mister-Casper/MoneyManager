package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeIntervalPickerDialog(
    defaultTimeIntervalController: TimeIntervalController,
    onAdd: (timeIntervalController: TimeIntervalController) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .customDialogModifier(CustomDialogPosition.BOTTOM),
        title = {
            Row(Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { onDismiss() }
                )
                Text(
                    text = stringResource(id = R.string.select_time_interval),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                )
            }
        },
        text = {
            TimeIntervalSelector(defaultTimeIntervalController) {
                onAdd(it)
                onDismiss()
            }
        },
        confirmButton = {}, properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
private fun TimeIntervalSelector(
    defaultTimeIntervalController: TimeIntervalController,
    onAdd: (timeIntervalController: TimeIntervalController) -> Unit = {},
) {
    val selectedOption = remember {
        mutableStateOf(defaultTimeIntervalController)
    }
    val intervals = TimeIntervalController.getItems(LocalContext.current)

    Column(Modifier.fillMaxWidth()) {
        LazyColumn {
            items(intervals.size) {
                val item = intervals[it]
                Row(
                    Modifier
                        .padding(4.dp)
                        .clickable {
                            selectedOption.value = item
                            onAdd(item)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = "",
                            Modifier
                                .align(Alignment.CenterVertically)
                                .size(32.dp),
                            tint = MaterialTheme.colors.secondary
                        )
                        Text(
                            text = stringResource(id = item.name),
                            Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                                .align(Alignment.CenterVertically),
                            fontSize = 18.sp,
                            color = MaterialTheme.colors.secondary
                        )
                        RadioButton(
                            selected = (item.icon == selectedOption.value.icon),
                            onClick = null,
                            Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}