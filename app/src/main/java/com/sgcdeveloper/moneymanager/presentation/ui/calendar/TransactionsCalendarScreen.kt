package com.sgcdeveloper.moneymanager.presentation.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.presentation.ui.composables.AutoSizeText
import com.sgcdeveloper.moneymanager.presentation.ui.composables.TimeIntervalControllerView
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.CalendarTransactionsDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.WalletPickerDialog

@Composable
fun TransactionsCalendarScreen(
    navController: NavController,
    transactionsCalendarViewModel: TransactionsCalendarViewModel
) {
    val state = remember { transactionsCalendarViewModel.state }.value

    if (state.dialogState is DialogState.WalletPickerDialog) {
        WalletPickerDialog(state.wallets, state.wallet, {
            transactionsCalendarViewModel.changeWallet(it)
        }, {
            transactionsCalendarViewModel.closeDialog()
        }, {
            navController.navigate(Screen.AddWallet(it).route)
        })
    } else if (state.dialogState is DialogState.CalendarTransactionsDialog) {
        CalendarTransactionsDialog(
            navController,
            state.dialogState.dayTransactions,
            {transactionsCalendarViewModel.moveDayBack()},
            {transactionsCalendarViewModel.moveDayNext()},
            {navController.navigate(Screen.AddDateTransaction(state.wallet,it).route)},
            { transactionsCalendarViewModel.closeDialog() })
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(top = 16.dp, bottom = 16.dp, start = 12.dp, end = 12.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "",
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(32.dp)
                    .clickable {
                        navController.popBackStack()
                    }
            )
            Row(
                Modifier
                    .align(Alignment.CenterVertically)
                    .clickable { transactionsCalendarViewModel.showWalletPickerDialog() }) {
                state.wallet?.let {
                    Text(
                        text = state.wallet.name,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 12.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    "",
                    Modifier.align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.Search,
                "",
                Modifier
                    .align(Alignment.CenterVertically)
                    .size(32.dp)
                    .clickable {
                        navController.navigate(Screen.SearchTransactionsScreen.route)
                    }
            )
        }
        Row(Modifier.fillMaxWidth()) {
            TimeIntervalControllerView(
                { transactionsCalendarViewModel.moveBack() },
                { transactionsCalendarViewModel.moveNext() },
                state.timeIntervalController.isCanMove(),
                state.description
            )
        }
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.income),
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
                Text(
                    text = state.transactionsCalendar.income, color = blue, fontSize = 18.sp, modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.expense),
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
                Text(
                    text = state.transactionsCalendar.expense, color = red, fontSize = 18.sp, modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.total),
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
                Text(
                    text = state.transactionsCalendar.total, fontSize = 18.sp, modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
            }
        }
        if (state.transactionsCalendar.days.isNotEmpty()) {
            Column(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 12.dp, end = 12.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    val days = state.transactionsCalendar.daysOfWeek
                    repeat(days.size) {
                        val day = days[it]
                        Text(text = day, fontSize = 16.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                }
                repeat(6) { row ->
                    Row(Modifier.weight(1f)) {
                        repeat(7) { column ->
                            val day = state.transactionsCalendar.days[row * 7 + column]
                            Column(
                                Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .border(BorderStroke(0.1.dp, MaterialTheme.colors.onBackground))
                                    .clickable {
                                        if (day.isExist)
                                            transactionsCalendarViewModel.showDayTransactionsDialog(day.dayTransactions)
                                    }
                            ) {
                                Text(text = day.number, fontSize = 12.sp)
                                AutoSizeText(
                                    text = day.income,
                                    color = blue
                                )
                                AutoSizeText(
                                    text = day.expense,
                                    color = red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}