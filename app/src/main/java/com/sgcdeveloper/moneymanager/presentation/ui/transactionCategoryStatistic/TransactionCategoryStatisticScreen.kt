package com.sgcdeveloper.moneymanager.presentation.ui.transactionCategoryStatistic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.sgcdeveloper.moneymanager.domain.model.CategoryStatistic
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.TransactionScreen
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.transactionNavigationButton
import com.sgcdeveloper.moneymanager.presentation.ui.composables.StatisticPieChart
import com.sgcdeveloper.moneymanager.presentation.ui.composables.TimeIntervalControllerView
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.TimeIntervalPickerDialog
import com.sgcdeveloper.moneymanager.presentation.ui.statistic.StatisticEvent
import com.sgcdeveloper.moneymanager.presentation.ui.statistic.StatisticViewModel
import com.sgcdeveloper.moneymanager.util.TimeInternalSingleton
import com.sgcdeveloper.moneymanager.util.WalletSingleton


@Composable
fun TransactionCategoryStatisticScreen(
    statisticViewModel: StatisticViewModel,
    navController: NavController,
    defaultScreen: TransactionScreen
) {
    val currentScreen = rememberSaveable { mutableStateOf(defaultScreen) }
    val dialog = remember { statisticViewModel.dialog }

    if (dialog.value is DialogState.SelectTimeIntervalDialog) {
        TimeIntervalPickerDialog(statisticViewModel.timeInterval.value, {
            statisticViewModel.onEvent(StatisticEvent.ChangeTimeInterval(it))
        }, {
            statisticViewModel.onEvent(StatisticEvent.CloseDialog)
        })
    }

    CheckDataFromAddTransactionScreen(navController, statisticViewModel)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 4.dp, top = 4.dp, end = 4.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            Box(Modifier.fillMaxWidth()) {
                Row {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable {
                                navController.popBackStack()
                            }
                    )
                    Text(
                        text = stringResource(id = R.string.structure),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp)
                            .weight(1f)
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.edit_calendar_icon),
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(32.dp)
                        .clickable { statisticViewModel.onEvent(StatisticEvent.ShowSelectTimeIntervalDialog) }
                )
            }
            Row(Modifier.fillMaxWidth()) {
                TimeIntervalControllerView(
                    { statisticViewModel.onEvent(StatisticEvent.MoveBack) },
                    { statisticViewModel.onEvent(StatisticEvent.MoveNext) },
                    statisticViewModel.timeInterval.value.isCanMove(),
                    statisticViewModel.description.value
                )
            }
            Row(Modifier.fillMaxWidth()) {
                transactionNavigationButton(
                    { currentScreen.value = it },
                    currentScreen.value,
                    TransactionScreen.Income,
                    stringResource(id = R.string.income)
                )
                transactionNavigationButton(
                    { currentScreen.value = it },
                    currentScreen.value,
                    TransactionScreen.Expense,
                    stringResource(id = R.string.expense)
                )
            }
            LazyColumn {
                if (currentScreen.value == TransactionScreen.Expense) {
                    item {
                        StatisticPieChart(
                            stringResource(id = R.string.expense_structure),
                            statisticViewModel.expenseEntries.value,
                            statisticViewModel.expenseColors.value,
                            { }, false
                        )
                    }
                    items(statisticViewModel.expenseStruct.value.size) {
                        val incomeCategory = statisticViewModel.expenseStruct.value[it]
                        TransactionCategoryItem(incomeCategory, navController, statisticViewModel)
                        Divider(modifier = Modifier.fillMaxSize())
                    }
                }

                if (currentScreen.value == TransactionScreen.Income) {
                    item {
                        StatisticPieChart(
                            stringResource(id = R.string.income_structure),
                            statisticViewModel.incomeEntries.value,
                            statisticViewModel.incomeColors.value,
                            { }, false
                        )
                    }
                    items(statisticViewModel.incomeStruct.value.size) {
                        val incomeCategory = statisticViewModel.incomeStruct.value[it]
                        TransactionCategoryItem(incomeCategory, navController, statisticViewModel)
                        Divider(modifier = Modifier.fillMaxSize())
                    }
                }

                item { Spacer(modifier = Modifier.padding(bottom = 55.dp)) }
            }
        }
    }
}

@Composable
fun TransactionCategoryItem(
    item: CategoryStatistic,
    navController: NavController,
    statisticViewModel: StatisticViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                TimeInternalSingleton.timeIntervalController = statisticViewModel.timeInterval.value
                navController.navigate(
                    Screen.TransactionCategoryTransactions(
                        WalletSingleton.wallet.value,
                        item.categoryEntry
                    ).route
                )
            }
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Row {
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
                    Text(text = item.category, fontSize = 16.sp, color = white)
                    Text(text = item.percent, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = white)
                }
            }
            Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                Text(
                    text = item.money,
                    textAlign = TextAlign.End,
                    modifier = Modifier.align(Alignment.End),
                    color = Color(item.moneyColor)
                )
                Text(
                    text = item.count,
                    textAlign = TextAlign.End,
                    modifier = Modifier.align(Alignment.End)
                )
            }
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