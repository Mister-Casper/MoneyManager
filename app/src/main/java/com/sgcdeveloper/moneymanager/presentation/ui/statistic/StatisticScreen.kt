package com.sgcdeveloper.moneymanager.presentation.ui.statistic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
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
    val wallet = remember { WalletSingleton.wallet }.observeAsState()
    val dialog = remember { statisticViewModel.dialog }

    if (dialog.value is DialogState.SelectTimeIntervalDialog) {
        TimeIntervalPickerDialog(statisticViewModel.timeInterval.value, {
            statisticViewModel.onEvent(StatisticEvent.ChangeTimeInterval(it))
        }, {
            statisticViewModel.onEvent(StatisticEvent.CloseDialog)
        })
    } else if (dialog.value is DialogState.WalletPickerDialog) {
        WalletPickerDialog(statisticViewModel.wallets.value, wallet.value, {
            statisticViewModel.onEvent(StatisticEvent.SetWallet(it))
        }, {
            statisticViewModel.onEvent(StatisticEvent.CloseDialog)
        })
    }

    CheckDataFromAddTransactionScreen(navController, statisticViewModel)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp)
            .padding(12.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            Box(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .clickable {
                            statisticViewModel.onEvent(StatisticEvent.ShowWalletPickerDialog)
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("wallet_id", -1L)
                        }) {
                    WalletSingleton.wallet.value?.let {
                        Text(
                            text = wallet.value!!.name,
                            fontSize = 22.sp,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            color = MaterialTheme.colors.secondary
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            "",
                            Modifier.align(Alignment.CenterVertically),
                            tint = MaterialTheme.colors.secondary
                        )
                    }
                }
                Row(Modifier.align(Alignment.CenterEnd)) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_calendar_icon),
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { statisticViewModel.onEvent(StatisticEvent.ShowSelectTimeIntervalDialog) }
                    )
                    Spacer(modifier = Modifier.padding(start = 12.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.settings_icon),
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
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
                    statisticViewModel.timeInterval.value.isCanMove(),
                    statisticViewModel.description.value
                )
            }
            LazyColumn(Modifier.padding(start = 12.dp, end = 12.dp)) {
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
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.overview),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 12.dp),
                                fontSize = 18.sp,
                                color = white
                            )
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.income),
                                    fontWeight = FontWeight.Thin,
                                    modifier = Modifier.weight(1f),
                                    color = white
                                )
                                Text(
                                    text = statisticViewModel.income.value,
                                    color = white
                                )
                            }
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.expense),
                                    fontWeight = FontWeight.Thin,
                                    modifier = Modifier.weight(1f),
                                    color = white
                                )
                                Text(text = statisticViewModel.expense.value, color = red)
                            }
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.total),
                                    fontWeight = FontWeight.Thin,
                                    modifier = Modifier.weight(1f),
                                    color = white
                                )
                                Text(
                                    text = statisticViewModel.total.value,
                                    color = white
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
                                        statisticViewModel.timeInterval.value
                                    navController.navigate(
                                        Screen.TimeIntervalTransactions(wallet.value).route
                                    )
                                }) {
                                Row(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(top = 8.dp, bottom = 8.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.show_more),
                                        fontWeight = FontWeight.Thin,
                                        modifier = Modifier
                                            .weight(1f)
                                            .align(Alignment.CenterVertically),
                                        color = white,
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
                        statisticViewModel.expenseEntries.value,
                        statisticViewModel.expenseColors.value,
                        {
                            navController.navigate(Screen.TransactionCategoryStatisticScreen(TransactionScreen.Expense).route)
                        }
                    )
                }

                item {
                    StatisticPieChart(
                        stringResource(id = R.string.income_structure),
                        statisticViewModel.incomeEntries.value,
                        statisticViewModel.incomeColors.value,
                        {
                            navController.navigate(Screen.TransactionCategoryStatisticScreen(TransactionScreen.Income).route)
                        }
                    )
                }

                item { Spacer(modifier = Modifier.padding(bottom = 80.dp)) }
            }
        }

        OutlinedButton(
            onClick = { navController.navigate(Screen.AddTransaction(wallet.value).route) },
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
    statisticViewModel: StatisticViewModel
) {
    val secondScreenResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<Long>("wallet_id")

    secondScreenResult?.let {
        if (secondScreenResult != -1L) {
            statisticViewModel.onEvent(StatisticEvent.ChangeWalletById(it))
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