package com.sgcdeveloper.moneymanager.presentation.ui.statistic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.TransactionScreen
import com.sgcdeveloper.moneymanager.presentation.ui.composables.StatisticPieChart
import com.sgcdeveloper.moneymanager.presentation.ui.composables.TimeIntervalControllerView
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.TimeIntervalPickerDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.WalletPickerDialog
import com.sgcdeveloper.moneymanager.util.TimeInternalSingleton
import com.sgcdeveloper.moneymanager.util.WalletSingleton


@Composable
fun StatisticScreen(
    statisticViewModel: StatisticViewModel,
    navController: NavController
) {
    val state = remember { statisticViewModel.state }.value

    if (state.dialogState is DialogState.SelectTimeIntervalDialog) {
        TimeIntervalPickerDialog(state.timeIntervalController, {
            statisticViewModel.onEvent(StatisticEvent.ChangeTimeInterval(it))
        }, {
            statisticViewModel.onEvent(StatisticEvent.CloseDialog)
        }, statisticViewModel.isDarkTheme())
    } else if (state.dialogState is DialogState.WalletPickerDialog) {
        WalletPickerDialog(state.wallets, state.wallet, {
            statisticViewModel.onEvent(StatisticEvent.SetWallet(it))
        }, {
            statisticViewModel.onEvent(StatisticEvent.CloseDialog)
        }, {
            navController.navigate(Screen.AddWallet(it).route)
        })
    }
    state.wallet?.let {
        CheckDataFromAddTransactionScreen(navController, statisticViewModel, state.wallet.walletId)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 56.dp)) {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                Row(
                    Modifier
                        .clickable {
                            statisticViewModel.onEvent(StatisticEvent.ShowWalletPickerDialog)
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("wallet_id", -1L)
                        }) {
                    if (state.wallet != null) {
                        Text(
                            text = state.wallet.name,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 12.dp),
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        "",
                        Modifier.align(Alignment.CenterVertically)
                    )
                }
                Row(
                    Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_calendar_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { statisticViewModel.onEvent(StatisticEvent.ShowSelectTimeIntervalDialog) }
                    )
                    Spacer(modifier = Modifier.padding(start = 12.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.settings_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { navController.navigate(Screen.Settings.route) }
                    )
                }
            }
            Row(Modifier.fillMaxWidth()) {
                TimeIntervalControllerView(
                    { statisticViewModel.onEvent(StatisticEvent.MoveBack) },
                    { statisticViewModel.onEvent(StatisticEvent.MoveNext) },
                    state.timeIntervalController.isCanMove(),
                    state.description
                )
            }
            LazyColumn(
                Modifier
                    .padding(start = 12.dp, end = 12.dp)
            ) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.overview),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 12.dp),
                                fontSize = 18.sp
                            )
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.income),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = state.income
                                )
                            }
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.expense),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(text = state.expense, color = red)
                            }
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.total),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = state.total
                                )
                            }
                            Divider(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .fillMaxSize()
                            )
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    TimeInternalSingleton.timeIntervalController =
                                        state.timeIntervalController
                                    navController.navigate(
                                        Screen.TimeIntervalTransactions(state.wallet).route
                                    )
                                }) {
                                Row(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(top = 8.dp, bottom = 8.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.show_more),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier
                                            .weight(1f)
                                            .align(Alignment.CenterVertically),
                                        fontSize = 20.sp
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowRight,
                                        contentDescription = "",
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    StatisticPieChart(
                        stringResource(id = R.string.expense_structure),
                        state.expenseEntries,
                        state.expenseColors,
                        {
                            navController.navigate(Screen.TransactionCategoryStatisticScreen(defaultScreen = TransactionScreen.Expense).route)
                        },
                        onWeeklyStatisticClick = {
                            navController.navigate(
                                Screen.WeeklyStatisticScreen(
                                    state.wallet,
                                    TransactionType.Expense
                                ).route
                            )
                        }
                    )
                }

                item {
                    StatisticPieChart(
                        stringResource(id = R.string.income_structure),
                        state.incomeEntries,
                        state.incomeColors,
                        {
                            navController.navigate(Screen.TransactionCategoryStatisticScreen(defaultScreen = TransactionScreen.Income).route)
                        },
                        onWeeklyStatisticClick = {
                            navController.navigate(
                                Screen.WeeklyStatisticScreen(
                                    state.wallet,
                                    TransactionType.Income
                                ).route
                            )
                        }
                    )
                }

                item { Spacer(modifier = Modifier.padding(bottom = 80.dp)) }
            }
        }

        OutlinedButton(
            onClick = { navController.navigate(Screen.AddTransaction(state.wallet).route) },
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
}

@Composable
fun CheckDataFromAddTransactionScreen(
    navController: NavController,
    statisticViewModel: StatisticViewModel,
    walletId: Long
) {
    val secondScreenResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<Long>("wallet_id")

    LaunchedEffect(Unit) {
        secondScreenResult?.let {
            if (walletId == 0L) {
                statisticViewModel.loadTransactions(WalletSingleton.wallet.value!!)
            }
            if (it != -1L) {
                statisticViewModel.onEvent(StatisticEvent.ChangeWalletById(it))
            }
        }
    }
}

var formatter: ValueFormatter = object : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return value.toInt().toString()
    }

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        return value.toInt().toString()
    }
}