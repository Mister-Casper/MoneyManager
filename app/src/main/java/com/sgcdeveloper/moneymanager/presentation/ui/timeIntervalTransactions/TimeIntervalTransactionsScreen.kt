package com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.*
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.TimeIntervalPickerDialog

@Composable
fun TimeIntervalTransactionsScreen(
    transactionsViewModel: TimeIntervalTransactionsViewModel,
    navController: NavController
) {
    val transactions = remember { transactionsViewModel.transactionItems }
    val dialog = remember { transactionsViewModel.dialog }

    if (dialog.value is DialogState.SelectTimeIntervalDialog) {
        TimeIntervalPickerDialog(transactionsViewModel.timeInterval.value, {
            transactionsViewModel.onEvent(TimeIntervalTransactionEvent.ChangeTimeInterval(it))
        }, {
            transactionsViewModel.onEvent(TimeIntervalTransactionEvent.CloseDialog)
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.padding(top = 4.dp)) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            transactionsViewModel.clear()
                            navController.popBackStack()
                        }
                )
                Text(
                    text = stringResource(id = R.string.transactions),
                    Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    fontSize = 24.sp,
                    color = MaterialTheme.colors.secondary
                )
                Icon(
                    painter = painterResource(id = R.drawable.edit_calendar_icon),
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(32.dp)
                        .clickable { transactionsViewModel.onEvent(TimeIntervalTransactionEvent.ShowSelectTimeIntervalDialog) }
                )
            }
            Row(Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Filled.ArrowLeft,
                    contentDescription = "",
                    tint = if (transactionsViewModel.timeInterval.value.isCanMove()) MaterialTheme.colors.secondary else gray,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(48.dp)
                        .clickable { transactionsViewModel.onEvent(TimeIntervalTransactionEvent.MoveBack) }
                )
                Text(
                    text = transactionsViewModel.description.value,
                    Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    color = MaterialTheme.colors.secondary
                )
                Icon(
                    imageVector = Icons.Filled.ArrowRight,
                    contentDescription = "",
                    tint = if (transactionsViewModel.timeInterval.value.isCanMove()) MaterialTheme.colors.secondary else gray,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(48.dp)
                        .clickable { transactionsViewModel.onEvent(TimeIntervalTransactionEvent.MoveNext) }
                )
            }
            LazyColumn(Modifier.padding(start = 12.dp, end = 12.dp)) {
                if (!transactionsViewModel.isEmpty.value) {
                    item {
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.overview),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 12.dp, top = 12.dp),
                                    fontSize = 18.sp,
                                    color = white
                                )
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, top = 12.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.income),
                                        fontWeight = FontWeight.Thin,
                                        modifier = Modifier.weight(1f),
                                        color = white
                                    )
                                    Text(
                                        text = transactionsViewModel.income.value,
                                        color = white
                                    )
                                }
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, top = 12.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.expense),
                                        fontWeight = FontWeight.Thin,
                                        modifier = Modifier.weight(1f),
                                        color = white
                                    )
                                    Text(text = transactionsViewModel.expense.value, color = red)
                                }
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, top = 12.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.total),
                                        fontWeight = FontWeight.Thin,
                                        modifier = Modifier.weight(1f),
                                        color = white
                                    )
                                    Text(
                                        text = transactionsViewModel.total.value,
                                        color = white
                                    )
                                }
                            }
                        }
                    }
                }
                items(transactions.value.size) {
                    val transactionItem = transactions.value[it]
                    if (transactionItem is BaseTransactionItem.TransactionHeader) {
                        com.sgcdeveloper.moneymanager.presentation.ui.transactions.TransactionHeader(transactionItem)
                    } else if (transactionItem is BaseTransactionItem.TransactionItem) {
                        com.sgcdeveloper.moneymanager.presentation.ui.transactions.TransactionItem(
                            transactionItem,
                            navController
                        )
                    }
                }
                item { Spacer(modifier = Modifier.padding(bottom = 55.dp)) }
            }
        }

        if (transactionsViewModel.isEmpty.value) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(Modifier.align(Alignment.Center)) {
                    Icon(
                        painter = painterResource(id = R.drawable.empty_icon),
                        contentDescription = "",
                        Modifier.align(Alignment.CenterHorizontally),
                        tint = MaterialTheme.colors.secondary
                    )
                    Text(
                        text = stringResource(id = R.string.no_transactions),
                        style = Typography.h5,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colors.secondary
                    )
                    Text(
                        text = stringResource(id = R.string.tap_to_add_transaction),
                        fontWeight = FontWeight.Thin,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }

        OutlinedButton(
            onClick = { navController.navigate(Screen.AddTransaction(transactionsViewModel.defaultWallet.value).route) },
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 8.dp, end = 8.dp)
                .align(Alignment.BottomEnd),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = blue)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add_icon),
                contentDescription = "",
                tint = white,
                modifier = Modifier.size(1000.dp)
            )
        }
    }

    BackHandler {
        transactionsViewModel.clear()
        navController.popBackStack()
    }
}

@Composable
fun TransactionHeader(header: BaseTransactionItem.TransactionHeader) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Text(
                text = header.dayNum,
                fontSize = 42.sp,
                modifier = Modifier.align(Alignment.CenterVertically),
                fontWeight = FontWeight.Bold,
                color = white
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = header.dayName, fontSize = 14.sp, fontWeight = FontWeight.Thin, color = white)
                Text(text = header.month, fontSize = 12.sp, fontWeight = FontWeight.Thin, color = white)
            }
            Text(text = header.money, Modifier.align(Alignment.CenterVertically), color = white)
        }
    }
    Divider(color = MaterialTheme.colors.background, thickness = 2.dp)
}

@Composable
fun TransactionItem(item: BaseTransactionItem.TransactionItem, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.EditTransaction(transaction = item.transactionEntry).route) }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Card(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterVertically),
                shape = RoundedCornerShape(8.dp),
            ) {
                Box(modifier = Modifier.background(Color(item.color))) {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = "",
                        Modifier
                            .align(Alignment.Center)
                            .size(40.dp),
                        tint = white
                    )
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = item.category, fontSize = 16.sp, fontWeight = FontWeight.Thin, color = white)
                Text(text = item.description, fontSize = 14.sp, fontWeight = FontWeight.Thin, color = white)
            }
            Text(
                text = item.money,
                modifier = Modifier.align(Alignment.CenterVertically),
                color = Color(item.moneyColor)
            )
        }
    }
}