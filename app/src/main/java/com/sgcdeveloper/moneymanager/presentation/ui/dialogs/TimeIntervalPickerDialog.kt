package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.util.Date
import java.time.LocalDate

var yOffset = 0

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimeIntervalPickerDialog(
    defaultTimeIntervalController: TimeIntervalController,
    onAdd: (timeIntervalController: TimeIntervalController) -> Unit = {},
    onDismiss: () -> Unit = {},
    isDarkTheme:Boolean
) {
    var isShowCreateCustomDialog by remember { mutableStateOf(false) }
    if(isShowCreateCustomDialog){
        val defaultStartDate = if(defaultTimeIntervalController is TimeIntervalController.CustomController){
            defaultTimeIntervalController.startDate
        }else
            Date(LocalDate.now())

        val defaultEndDate = if(defaultTimeIntervalController is TimeIntervalController.CustomController){
            defaultTimeIntervalController.endDate
        }else
            Date(LocalDate.now().plusDays(7))

        SelectTimeIntervalDialog({isShowCreateCustomDialog = false},defaultStartDate,defaultEndDate,{start,end->
            val timeController = TimeIntervalController.CustomController
            timeController.startDate = start
            timeController.endDate = end
            onAdd(timeController)
            onDismiss()
        },isDarkTheme)
    }
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if(offset.y <= yOffset)
                        onDismiss()
                }
            }
            .customtDialogModifier(),
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
            TimeIntervalSelector(defaultTimeIntervalController, {
                onAdd(it)
                onDismiss()
            }) {
                isShowCreateCustomDialog = true
            }
        },
        confirmButton = {}, properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

fun Modifier.customtDialogModifier() = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    yOffset = constraints.maxHeight - placeable.height
    layout(constraints.maxWidth, constraints.maxHeight) {
        placeable.place(0, yOffset, 10f)
    }
}

@Composable
private fun TimeIntervalSelector(
    defaultTimeIntervalController: TimeIntervalController,
    onAdd: (timeIntervalController: TimeIntervalController) -> Unit = {},
    onCreateCustom: () -> Unit = {}
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
                            if (item is TimeIntervalController.CustomController) {
                                onCreateCustom()
                                return@clickable
                            }
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