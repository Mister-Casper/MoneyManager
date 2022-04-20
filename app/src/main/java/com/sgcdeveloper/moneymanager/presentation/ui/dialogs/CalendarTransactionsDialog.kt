package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.calendar.DayTransactions
import com.sgcdeveloper.moneymanager.presentation.ui.transactions.TransactionItem
import com.sgcdeveloper.moneymanager.util.Date

var ywOffset11 = 0

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CalendarTransactionsDialog(
    navController: NavController,
    dayTransactions: DayTransactions,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onAdd: (date: String) -> Unit,
    onDismiss: () -> Unit
) {
    val height = remember{ mutableStateOf(0.dp)}

    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .pointerInput(Unit) {
                detectTapGestures { ywOffset ->
                    if (ywOffset.y <= ywOffset11)
                        onDismiss()
                }
            }
            .customDialogModifier11(height)
            .heightIn(0.dp,height.value),
        title = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = dayTransactions.dayText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = dayTransactions.total,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                LazyColumn(Modifier.fillMaxWidth().weight(3f)) {
                    item {
                        Divider(color = MaterialTheme.colors.onSurface)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    if (dayTransactions.transactions.isNotEmpty()) {
                        items(dayTransactions.transactions.size) {
                            val transaction = dayTransactions.transactions[it]
                            TransactionItem(
                                item = transaction,
                                navController = navController,
                                isMultiSelection = false,
                                onChangedSelection = {}
                            ) {

                            }
                        }
                    } else
                        item {
                            Text(
                                text = stringResource(id = R.string.no_transactions_yet),
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "move to the back day",
                        modifier = Modifier
                            .align(
                                Alignment.CenterVertically
                            )
                            .weight(1f)
                            .clickable {
                                onBack()
                            }
                            .size(40.dp)
                    )
                    Button(
                        onClick = { onAdd(Date(dayTransactions.day).epochMillis.toString()) },
                        modifier = Modifier
                            .align(
                                Alignment.CenterVertically
                            )
                            .padding(start = 16.dp, end = 16.dp)
                            .weight(10f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.add_day_transaction),
                            fontSize = 20.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "move to the next day",
                        modifier = Modifier
                            .align(
                                Alignment.CenterVertically
                            )
                            .weight(1f)
                            .clickable {
                                onNext()
                            }
                            .size(40.dp)
                    )
                }
            }
        },
        confirmButton = {}, properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

fun Modifier.customDialogModifier11(height: MutableState<Dp>) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    ywOffset11 = kotlin.math.max(constraints.maxHeight  - placeable.height,constraints.maxHeight/2)
    height.value = constraints.maxHeight.toDp() / 2
    layout(constraints.maxWidth, constraints.maxHeight) {
        placeable.place(0, ywOffset11, 10f)
    }
}