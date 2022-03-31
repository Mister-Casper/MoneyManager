package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.Recurring.*
import com.sgcdeveloper.moneymanager.domain.model.RecurringEndType
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.gray
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.InputEditText
import com.sgcdeveloper.moneymanager.util.Date
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.*

var ywOffset1 = 0

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun RecurringDialogPicker(
    defaultRecurringInterval: RecurringInterval = RecurringInterval.None,
    date: Date = Date(LocalDateTime.now()),
    firstDay: DayOfWeek = DayOfWeek.MONDAY,
    onAdd: (recurring: RecurringInterval) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    var repeatInterval by remember { mutableStateOf(defaultRecurringInterval.repeatInterval.toString()) }
    var isSameDay by remember { mutableStateOf(defaultRecurringInterval.sameDay) }
    val selectedDay = rememberMutableStateListOf<DayOfWeek>()
    selectedDay.addAll(defaultRecurringInterval.days)
    var expanded by remember { mutableStateOf(false) }
    val suggestions = RecurringEndType.values()
    var selectedRecurringType by remember { mutableStateOf(defaultRecurringInterval.type) }
    var times by remember { mutableStateOf(defaultRecurringInterval.times.toString()) }
    var until by remember { mutableStateOf(defaultRecurringInterval.endDate) }

    val recurringList = values()
    var selectedRecurring by remember { mutableStateOf(defaultRecurringInterval.recurring) }

    fun getEndDate(): Date {
        return when (selectedRecurring) {
            None -> throw Exception()
            Daily -> {
                if (selectedRecurringType == RecurringEndType.Until || selectedRecurringType == RecurringEndType.Forever) {
                    until
                } else {
                    Date(date.getAsLocalDate().plusDays(times.toLong()))
                }
            }
            Weekly -> {
                if (selectedRecurringType == RecurringEndType.Until || selectedRecurringType == RecurringEndType.Forever) {
                    until
                } else {
                    var happened = 0
                    var i = 0L
                    val endDate = date.getAsLocalDate()
                    while (happened != times.toInt()) {
                        if (selectedDay.contains(endDate.dayOfWeek))
                            happened++
                        endDate.plusDays(1)
                        i++
                    }
                    Date(endDate)
                }
            }
            Monthly -> {
                if (selectedRecurringType == RecurringEndType.Until || selectedRecurringType == RecurringEndType.Forever) {
                    until
                } else {
                    if (isSameDay != -1) {
                        val endDate = date.getAsLocalDate().plusMonths(times.toLong())
                        endDate.withDayOfMonth(endDate.lengthOfMonth())
                    }
                    Date(date.getAsLocalDate().plusMonths(times.toLong()))
                }
            }
            Yearly -> {
                if (selectedRecurringType == RecurringEndType.Until || selectedRecurringType == RecurringEndType.Forever) {
                    until
                } else {
                    Date(date.getAsLocalDate().plusYears(times.toLong()))
                }
            }
        }
    }

    fun isCanDone(): Boolean {
        if (selectedRecurring == None)
            return true
        if (selectedRecurring == Weekly && selectedDay.isEmpty())
            return false
        if (!repeatInterval.isDigitsOnly() || repeatInterval.isEmpty())
            return false
        if (selectedRecurringType == RecurringEndType.For && (!times.isDigitsOnly() || times.isEmpty()))
            return false
        return true
    }

    fun getRecurringInterval(): RecurringInterval {
        return when (selectedRecurring) {
            None -> RecurringInterval.None
            Daily -> RecurringInterval.Daily(
                null,
                selectedRecurringType == RecurringEndType.Forever,
                getEndDate(),
                repeatInterval.toInt(),
                times.toInt(),
                selectedRecurringType
            )
            Weekly -> RecurringInterval.Weekly(
                selectedDay,
                null,
                selectedRecurringType == RecurringEndType.Forever,
                getEndDate(),
                repeatInterval.toInt(),
                times.toInt(),
                selectedRecurringType
            )
            Monthly -> RecurringInterval.Monthly(
                isSameDay,
                null,
                selectedRecurringType == RecurringEndType.Forever,
                getEndDate(),
                repeatInterval.toInt(),
                times.toInt(),
                selectedRecurringType
            )
            Yearly -> RecurringInterval.Yearly(
                null,
                selectedRecurringType == RecurringEndType.Forever,
                getEndDate(),
                repeatInterval.toInt(),
                times.toInt(),
                selectedRecurringType
            )
        }
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
                    if (offset.y <= ywOffset1)
                        onDismiss()
                }
            }
            .customDialogModifier1(),
        title = {
            Box(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.recurring),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 26.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(start = 8.dp)
                )
            }
        },
        text = {
            LazyColumn(Modifier.fillMaxWidth()) {
                items(recurringList.size) {
                    val item = recurringList[it]
                    Column(
                        Modifier
                            .clickable {
                                selectedRecurring = item
                            },
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = stringResource(id = item.titleRes),
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colors.secondary,
                                fontSize = 20.sp
                            )
                            RadioButton(
                                selected = (item == selectedRecurring),
                                onClick = null
                            )
                        }
                        if (item == selectedRecurring) {
                            if (item != None) {
                                Row(Modifier.fillMaxWidth()) {
                                    Text(
                                        text = stringResource(id = R.string.every),
                                        color = MaterialTheme.colors.secondary,
                                        fontSize = 20.sp,
                                        modifier = Modifier
                                            .align(Alignment.Top)
                                            .padding(start = 20.dp)
                                    )
                                    Column(
                                        modifier = Modifier
                                            .padding(start = 4.dp, end = 4.dp)
                                            .height(24.dp)
                                            .widthIn(30.dp, 80.dp)
                                            .align(Alignment.Bottom)
                                    ) {
                                        InputEditText(
                                            value = repeatInterval,
                                            onValueChange = { num ->
                                                if (num.length <= 3) repeatInterval =
                                                    num.filter { symbol -> symbol.isDigit() }
                                            },
                                            singleLine = true,
                                            contentTextStyle = TextStyle(
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colors.secondary,
                                                textAlign = TextAlign.Center,
                                            ),
                                            cursorColor = MaterialTheme.colors.secondary,
                                            hintTextStyle = TextStyle(),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier
                                        )
                                        Divider()
                                    }
                                    Text(
                                        text = stringResource(id = item.nameRes),
                                        color = MaterialTheme.colors.secondary,
                                        fontSize = 20.sp,
                                        modifier = Modifier
                                            .weight(1f)
                                            .align(Alignment.Top)
                                    )
                                }
                            }
                            if (item == Weekly) {
                                LazyRow(Modifier.padding(top = 8.dp)) {
                                    items(7) {
                                        val dayNumber = 1 + ((firstDay.value - 1 + it) % 7)
                                        val day = DayOfWeek.of(dayNumber)
                                        Box(modifier = Modifier.padding(2.dp)) {
                                            Box(
                                                Modifier
                                                    .background(
                                                        color = if (selectedDay.contains(day)) blue else MaterialTheme.colors.background,
                                                        shape = androidx.compose.foundation.shape.CircleShape
                                                    )
                                                    .border(
                                                        BorderStroke(1.dp, gray),
                                                        androidx.compose.foundation.shape.CircleShape
                                                    )
                                                    .clickable {
                                                        if (selectedDay.contains(day))
                                                            selectedDay.remove(day)
                                                        else
                                                            selectedDay.add(day)
                                                    }
                                                    .size(40.dp)
                                            ) {
                                                Text(
                                                    text = day.getDisplayName(
                                                        java.time.format.TextStyle.SHORT,
                                                        Locale.getDefault()
                                                    ),
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.align(Alignment.Center),
                                                    color = if (selectedDay.contains(day)) white else MaterialTheme.colors.secondary,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            } else if (item == Monthly) {
                                Row(
                                    Modifier
                                        .padding(top = 16.dp, start = 20.dp)
                                        .fillMaxWidth()
                                        .clickable { isSameDay = date.getAsLocalDate().dayOfMonth }) {
                                    RadioButton(
                                        selected = isSameDay != -1,
                                        onClick = null
                                    )
                                    Text(
                                        text = stringResource(
                                            id = R.string.same_day_each_month,
                                            date.getAsLocalDate().dayOfMonth
                                        ), fontSize = 20.sp, color = MaterialTheme.colors.secondary
                                    )
                                }
                                Row(
                                    Modifier
                                        .padding(top = 8.dp, start = 20.dp)
                                        .fillMaxWidth()
                                        .clickable { isSameDay = -1 }) {
                                    RadioButton(
                                        selected = isSameDay == -1,
                                        onClick = null,
                                        Modifier.align(Alignment.CenterVertically)
                                    )
                                    Text(
                                        text = stringResource(id = R.string.end_of_month),
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colors.secondary,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }

                            if (item != None) {
                                ExposedDropdownMenuBox(
                                    modifier = Modifier
                                        .padding(start = 10.dp, top = 12.dp),
                                    expanded = expanded,
                                    onExpandedChange = {}
                                ) {
                                    Row(Modifier.fillMaxWidth()) {
                                        val source = remember { MutableInteractionSource() }

                                        if (source.collectIsPressedAsState().value) {
                                            expanded = !expanded
                                        }

                                        TextField(
                                            readOnly = true,
                                            value = stringResource(id = selectedRecurringType.nameRes),
                                            onValueChange = { },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = expanded,
                                                    onIconClick = { expanded = !expanded }
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .align(Alignment.CenterVertically),
                                            colors = ExposedDropdownMenuDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                                            interactionSource = source
                                        )
                                        if (selectedRecurringType == RecurringEndType.For) {
                                            Column(
                                                modifier = Modifier
                                                    .padding(start = 12.dp, end = 4.dp)
                                                    .height(24.dp)
                                                    .widthIn(30.dp, 50.dp)
                                                    .align(Alignment.CenterVertically)
                                            ) {
                                                InputEditText(
                                                    value = times,
                                                    onValueChange = { num ->
                                                        if (num.length <= 3) times =
                                                            num.filter { symbol -> symbol.isDigit() }
                                                    },
                                                    singleLine = true,
                                                    contentTextStyle = TextStyle(
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colors.secondary,
                                                        textAlign = TextAlign.Center,
                                                    ),
                                                    cursorColor = MaterialTheme.colors.secondary,
                                                    hintTextStyle = TextStyle(),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    modifier = Modifier
                                                )
                                                Divider()
                                            }
                                            Text(
                                                text = stringResource(id = R.string.times),
                                                color = MaterialTheme.colors.secondary,
                                                fontSize = 20.sp,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .align(Alignment.CenterVertically)
                                            )
                                        } else if (selectedRecurringType == RecurringEndType.Until) {
                                            var dateExpanded by remember { mutableStateOf(false) }
                                            ExposedDropdownMenuBox(
                                                modifier = Modifier
                                                    .padding(start = 10.dp)
                                                    .align(Alignment.CenterVertically)
                                                    .weight(1.5f),
                                                expanded = dateExpanded,
                                                onExpandedChange = {
                                                    dateExpanded = !dateExpanded
                                                }
                                            ) {
                                                TextField(
                                                    readOnly = true,
                                                    value = until.toDateString(),
                                                    onValueChange = { },
                                                    trailingIcon = {
                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                            expanded = dateExpanded
                                                        )
                                                    },
                                                    colors = ExposedDropdownMenuDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                                                )
                                                if (dateExpanded) {
                                                    DatePicker(
                                                        defaultDate = until,
                                                        onDateSelected = {
                                                            until = Date(it)
                                                            dateExpanded = false
                                                        },
                                                        onDismissRequest = { dateExpanded = false })
                                                }
                                            }
                                        }
                                    }
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = {
                                            expanded = false
                                        }
                                    ) {
                                        suggestions.forEach { selectionOption ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    selectedRecurringType = selectionOption
                                                    expanded = false
                                                }
                                            ) {
                                                Text(text = stringResource(id = selectionOption.nameRes))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel), color = white)
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(getRecurringInterval()) }, enabled = isCanDone()) {
                Text(text = stringResource(id = R.string.done), color = white)
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

fun Modifier.customDialogModifier1() = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    ywOffset1 = constraints.maxHeight - placeable.height
    layout(constraints.maxWidth, constraints.maxHeight) {
        placeable.place(0, ywOffset1, 10f)
    }
}