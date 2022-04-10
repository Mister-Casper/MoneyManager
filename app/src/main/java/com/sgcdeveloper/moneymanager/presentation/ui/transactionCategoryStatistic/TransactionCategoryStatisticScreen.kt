package com.sgcdeveloper.moneymanager.presentation.ui.transactionCategoryStatistic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.CategoryStatistic
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
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
    val state = remember { statisticViewModel.state }.value
    val currentScreen = rememberSaveable { mutableStateOf(defaultScreen) }
    val context = LocalContext.current

    if (state.dialogState is DialogState.SelectTimeIntervalDialog) {
        TimeIntervalPickerDialog(state.timeIntervalController, {
            statisticViewModel.onEvent(StatisticEvent.ChangeTimeInterval(it))
        }, {
            statisticViewModel.onEvent(StatisticEvent.CloseDialog)
        }, statisticViewModel.isDarkTheme())
    }

    state.wallet?.let {
        CheckDataFromAddTransactionScreen(navController, statisticViewModel, state.wallet.walletId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            Box(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.surface)
                        .padding(top = 16.dp, bottom = 16.dp)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .padding(start = 12.dp)
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
                            .padding(start = 12.dp)
                            .weight(1f)
                    )
                }
                Row(
                    Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = rememberImagePainter(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.edit_calendar_icon
                            )
                        ),
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { statisticViewModel.onEvent(StatisticEvent.ShowSelectTimeIntervalDialog) }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
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
                            state.expenseEntries,
                            state.expenseColors,
                            { }, false,
                            {
                                navController.navigate(
                                    Screen.WeeklyStatisticScreen(
                                        state.wallet,
                                        TransactionType.Expense
                                    ).route
                                )
                            }
                        )
                    }
                    items(state.expenseStruct.size) {
                        val incomeCategory = state.expenseStruct[it]
                        TransactionCategoryItem(incomeCategory, navController, statisticViewModel)
                        Divider(modifier = Modifier.fillMaxSize())
                    }
                }

                if (currentScreen.value == TransactionScreen.Income) {
                    item {
                        StatisticPieChart(
                            stringResource(id = R.string.income_structure),
                            state.incomeEntries,
                            state.incomeColors,
                            { }, false, {
                                navController.navigate(
                                    Screen.WeeklyStatisticScreen(
                                        state.wallet,
                                        TransactionType.Income
                                    ).route
                                )
                            }
                        )
                    }
                    items(state.incomeStruct.size) {
                        val incomeCategory = state.incomeStruct[it]
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
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                TimeInternalSingleton.timeIntervalController = statisticViewModel.state.value.timeIntervalController
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
                            painter = rememberImagePainter(ContextCompat.getDrawable(context, item.icon)),
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
                    Text(text = item.category, fontSize = 16.sp)
                    Text(text = item.percent + " %", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
            Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                Text(
                    text = item.money,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.End),
                    color = if (item.moneyColor != Color.Unspecified.toArgb()) Color(item.moneyColor) else MaterialTheme.colors.onBackground
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
    statisticViewModel: StatisticViewModel,
    walletId: Long
) {
    val secondScreenResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<Long>("wallet_id")

    secondScreenResult?.let {
        if (walletId == 0L) {
            statisticViewModel.loadTransactions(WalletSingleton.wallet.value!!)
            return
        }
        if (secondScreenResult != -1L) {
            statisticViewModel.onEvent(StatisticEvent.ChangeWalletById(it))
        }
    }
}