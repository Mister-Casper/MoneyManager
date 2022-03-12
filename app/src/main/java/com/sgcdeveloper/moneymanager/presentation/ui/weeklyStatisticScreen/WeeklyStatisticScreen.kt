package com.sgcdeveloper.moneymanager.presentation.ui.weeklyStatisticScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.TimeIntervalControllerView


@Composable
fun WeeklyStatisticScreen(navController: NavController, weeklyStatisticViewModel: WeeklyStatisticViewModel) {
    val textColor = MaterialTheme.colors.secondary

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(start = 4.dp, top = 4.dp, end = 4.dp)
    ) {
        item {
            Row {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(40.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
                Text(
                    text = weeklyStatisticViewModel.title.value,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                        .weight(1f)
                )
            }
            Row {
                TimeIntervalControllerView(
                    { weeklyStatisticViewModel.onEvent(WeeklyStatisticScreenEvent.MoveBack) },
                    { weeklyStatisticViewModel.onEvent(WeeklyStatisticScreenEvent.MoveNext) },
                    true,
                    weeklyStatisticViewModel.timeIntervalDescription.value
                )
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.total),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                Text(
                    text = weeklyStatisticViewModel.sum.value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                AndroidView(factory = { ctx ->
                    BarChart(ctx).apply {
                        val dataSet = BarDataSet(weeklyStatisticViewModel.empties.value, "")
                        val data = BarData(dataSet)
                        this.data = data
                        this.axisLeft.textColor = white.toArgb()
                        this.axisRight.isEnabled = false
                        this.legend.isEnabled = false
                        this.xAxis.valueFormatter = LabelFormatter(weeklyStatisticViewModel.labels)
                        this.barData.setValueFormatter(LabelFormatter(weeklyStatisticViewModel.labels))
                        this.xAxis.textColor = textColor.toArgb()
                        this.xAxis.setDrawGridLines(false)
                        this.setScaleEnabled(false)
                        this.description.isEnabled = false
                    }
                }, modifier = Modifier
                    .height(180.dp)
                    .padding(top = 12.dp)
                    .fillMaxWidth(), update = {
                    val dataSet = BarDataSet(weeklyStatisticViewModel.empties.value, "")
                    dataSet.color = weeklyStatisticViewModel.rowColor
                    val data = BarData(dataSet)
                    it.axisLeft.textColor = textColor.toArgb()
                    it.data = data
                    it.xAxis.setDrawGridLines(false)
                    it.xAxis.valueFormatter = LabelFormatter(weeklyStatisticViewModel.labels)
                    it.barData.setValueFormatter(LabelFormatter(weeklyStatisticViewModel.labels))
                    it.xAxis.textColor = textColor.toArgb()
                    it.setScaleEnabled(false)
                    it.description.isEnabled = false
                    it.invalidate()
                })
            }
        }
        items(weeklyStatisticViewModel.days.value.size) {
            val day = weeklyStatisticViewModel.days.value[it]
            Column(Modifier.fillMaxWidth()) {
                Divider()
                Box(Modifier.fillMaxWidth()) {
                    Column(Modifier.align(Alignment.CenterStart)) {
                        Text(text = day.dayName, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = textColor)
                        Text(text = day.dateName, fontSize = 18.sp, fontWeight = FontWeight.Light, color = textColor)
                    }
                    Column(Modifier.align(Alignment.CenterEnd)) {
                        val sumColor = if(day.colorSum != 0)
                            Color(day.colorSum)
                        else
                            MaterialTheme.colors.secondary
                        Text(
                            text = day.sum,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = sumColor,
                            modifier = Modifier.align(Alignment.End)
                        )
                        Text(
                            text = day.countTransactions,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Light,
                            color = textColor,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}

class LabelFormatter(private val mLabels: MutableList<String>) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if (mLabels.isEmpty())
            return ""
        return mLabels[value.toInt()]
    }

    override fun getBarLabel(barEntry: BarEntry?): String {
        return ""
    }
}