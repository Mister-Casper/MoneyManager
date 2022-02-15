package com.sgcdeveloper.moneymanager.presentation.ui.statistic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.gray
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.TimeIntervalPickerDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.WalletPickerDialog
import com.sgcdeveloper.moneymanager.util.TimeInternalSingleton


@Composable
fun StatisticScreen(
    statisticViewModel: StatisticViewModel,
    navController: NavController
) {
    val wallet = remember { statisticViewModel.defaultWallet }
    val dialog = remember { statisticViewModel.dialog }

    if (dialog.value is DialogState.SelectTimeIntervalDialog) {
        TimeIntervalPickerDialog(statisticViewModel.timeInterval.value, {
            statisticViewModel.onEvent(StatisticEvent.ChangeTimeInterval(it))
        }, {
            statisticViewModel.onEvent(StatisticEvent.CloseDialog)
        })
    } else if (dialog.value is DialogState.WalletPickerDialog) {
        WalletPickerDialog(statisticViewModel.wallets.value, statisticViewModel.defaultWallet.value, {
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
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
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
                    wallet.value?.let {
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
                Icon(
                    imageVector = Icons.Filled.ArrowLeft,
                    contentDescription = "",
                    tint = if (statisticViewModel.timeInterval.value.isCanMove()) MaterialTheme.colors.secondary else gray,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(48.dp)
                        .clickable { statisticViewModel.onEvent(StatisticEvent.MoveBack) }
                )
                Text(
                    text = statisticViewModel.description.value,
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
                    tint = if (statisticViewModel.timeInterval.value.isCanMove()) MaterialTheme.colors.secondary else gray,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(48.dp)
                        .clickable { statisticViewModel.onEvent(StatisticEvent.MoveNext) }
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
                                        Screen.TimeIntervalTransactions(statisticViewModel.defaultWallet.value).route
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
                    val textColor = MaterialTheme.colors.secondary
                    Card(
                        Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                            .padding(top = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(top = 12.dp, start = 18.dp)) {
                            Text(
                                text = stringResource(id = R.string.expense_structure), fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = white
                            )
                            AndroidView(factory = { ctx ->
                                PieChart (ctx).apply {
                                    val dataSet = PieDataSet(statisticViewModel.expenseEntries.value, "")
                                    dataSet.colors = statisticViewModel.expenseColors.value
                                    dataSet.valueTextColor = white.toArgb()
                                    dataSet.valueTextSize = 14f
                                    dataSet.valueFormatter = formatter
                                    val data = PieData(dataSet)
                                    this.setDrawEntryLabels(false)
                                    this.data = data
                                    this.invalidate()
                                    this.holeRadius = 75.0F
                                    this.description.isEnabled = false
                                    this.setHoleColor(Color.Transparent.toArgb())
                                    this.legend.textColor = textColor.toArgb()
                                    this.legend.textSize = 14f
                                    this.legend.isWordWrapEnabled = true
                                }
                            }, modifier = Modifier
                                .size(300.dp)
                                .fillMaxWidth(), update = {
                                val dataSet = PieDataSet(statisticViewModel.expenseEntries.value, "")
                                dataSet.colors = statisticViewModel.expenseColors.value
                                dataSet.valueTextColor = white.toArgb()
                                dataSet.valueTextSize = 14f
                                dataSet.valueFormatter = formatter
                                val data = PieData(dataSet)
                                it.data = data
                                it.invalidate()
                            })
                            Divider(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .fillMaxSize()
                            )
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

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
                    val textColor = MaterialTheme.colors.secondary
                    Card(
                        Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                            .padding(top = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(top = 12.dp, start = 18.dp)) {
                            Text(
                                text = stringResource(id = R.string.expense_structure), fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = white
                            )
                            AndroidView(factory = { ctx ->
                                PieChart (ctx).apply {
                                    val dataSet = PieDataSet(statisticViewModel.incomeEntries.value, "")
                                    dataSet.colors = statisticViewModel.incomeColors.value
                                    dataSet.valueTextColor = white.toArgb()
                                    dataSet.valueTextSize = 14f
                                    dataSet.valueFormatter = formatter
                                    val data = PieData(dataSet)
                                    this.setDrawEntryLabels(false)
                                    this.data = data
                                    this.invalidate()
                                    this.holeRadius = 75.0F
                                    this.description.isEnabled = false
                                    this.setHoleColor(Color.Transparent.toArgb())
                                    this.legend.textColor = textColor.toArgb()
                                    this.legend.textSize = 14f
                                    this.legend.isWordWrapEnabled = true
                                }
                            }, modifier = Modifier
                                .size(300.dp)
                                .fillMaxWidth(), update = {
                                val dataSet = PieDataSet(statisticViewModel.incomeEntries.value, "")
                                dataSet.colors = statisticViewModel.incomeColors.value
                                dataSet.valueTextColor = white.toArgb()
                                dataSet.valueTextSize = 14f
                                dataSet.valueFormatter = formatter
                                val data = PieData(dataSet)
                                it.data = data
                                it.invalidate()
                            })
                            Divider(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .fillMaxSize()
                            )
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

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

                item { Spacer(modifier = Modifier.padding(bottom = 55.dp)) }
            }
        }

        OutlinedButton(
            onClick = { navController.navigate(Screen.AddTransaction(statisticViewModel.defaultWallet.value).route) },
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

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return ""
    }

    override fun getBarLabel(barEntry: BarEntry?): String {
        return ""
    }

    override fun getBarStackedLabel(value: Float, stackedEntry: BarEntry?): String {
        return ""
    }

    override fun getBubbleLabel(bubbleEntry: BubbleEntry?): String {
        return ""
    }

    override fun getCandleLabel(candleEntry: CandleEntry?): String {
        return ""
    }

    override fun getPointLabel(entry: Entry?): String {
        return ""
    }

    override fun getRadarLabel(radarEntry: RadarEntry?): String {
        return ""
    }
}