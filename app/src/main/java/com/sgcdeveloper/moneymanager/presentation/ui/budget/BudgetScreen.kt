package com.sgcdeveloper.moneymanager.presentation.ui.budget

import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BaseBudget
import com.sgcdeveloper.moneymanager.presentation.theme.dark_gray
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.RoundedLinearProgressIndicator
import com.sgcdeveloper.moneymanager.presentation.ui.util.BudgetMarkerView

@Composable
fun BudgetScreen(budget: BaseBudget.BudgetItem, navController: NavController) {
    Column(Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.align(Alignment.CenterStart)) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(40.dp)
                        .clickable { navController.popBackStack() }
                )
                Text(
                    text = stringResource(id = R.string.budget_view),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                        .weight(1f)
                )
            }
            Row(Modifier.align(Alignment.CenterEnd)) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_icon),
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(40.dp)
                        .clickable { navController.popBackStack() }
                )
                Icon(
                    painter = painterResource(id = R.drawable.delete_icon),
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(40.dp)
                        .clickable { navController.popBackStack() })
            }
        }
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            item {
                Text(text = budget.budgetName, fontSize = 24.sp, color = MaterialTheme.colors.secondary)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.spent),
                        modifier = Modifier.align(Alignment.CenterStart),
                        color = MaterialTheme.colors.secondary
                    )
                    Text(
                        text = stringResource(id = budget.leftStrRes, ""),
                        modifier = Modifier.align(Alignment.CenterEnd),
                        color = MaterialTheme.colors.secondary
                    )
                }
                Box(Modifier.fillMaxWidth()) {
                    Text(text = budget.spent, Modifier.align(Alignment.CenterStart), fontWeight = FontWeight.Bold,color = MaterialTheme.colors.secondary)
                    Text(text = budget.left, Modifier.align(Alignment.CenterEnd), fontWeight = FontWeight.Bold,color = MaterialTheme.colors.secondary)
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    RoundedLinearProgressIndicator(
                        height = 20.dp,
                        progress = budget.progress,
                        color = Color(budget.color),
                        backgroundColor = dark_gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 12.dp)
                    )
                    Text(
                        text = budget.progressPercent + "%",
                        modifier = Modifier.align(Alignment.Center),
                        color = white
                    )
                }
                Column {
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(id = R.string.budget_category),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.weight(0.3f),
                            color = MaterialTheme.colors.secondary
                        )
                        Text(
                            text = budget.categoryDescription,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(0.7f),
                            color = MaterialTheme.colors.secondary
                        )
                    }
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(id = R.string.budget_budget),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.weight(0.3f),
                            color = MaterialTheme.colors.secondary
                        )
                        Text(
                            text = budget.budget,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(0.7f),
                            color = MaterialTheme.colors.secondary
                        )
                    }
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(id = R.string.budget_period),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.weight(0.3f),
                            color = MaterialTheme.colors.secondary
                        )
                        Text(
                            text = budget.periodDescription,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(0.7f),
                            color = MaterialTheme.colors.secondary
                        )
                    }
                }

                val mv = BudgetMarkerView(LocalContext.current, R.layout.budget_marker_view)
                val textColor = MaterialTheme.colors.secondary

                AndroidView(factory = { ctx ->
                    LineChart(ctx).apply {
                        val lineEntries = budget.graphEntries
                        val lineDataSet = LineDataSet(lineEntries, "")
                        lineDataSet.setDrawFilled(true)
                        lineDataSet.color = budget.color
                        lineDataSet.valueTextColor =  Color.Unspecified.toArgb()
                        lineDataSet.fillDrawable = GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            intArrayOf(Color(budget.color).copy(alpha = 0.2f).toArgb(), budget.color)
                        )
                        val data = LineData(lineDataSet)
                        this.data = data
                        this.axisLeft.textColor = white.toArgb()
                        this.axisRight.isEnabled = false
                        this.legend.isEnabled = false
                        this.xAxis.setDrawGridLines(false)
                        this.setScaleEnabled(false)
                        this.description.isEnabled = false
                        this.marker = mv
                        this.axisLeft.textColor = textColor.toArgb()
                        this.xAxis.textColor = Color.Unspecified.toArgb()
                    }
                }, modifier = Modifier
                    .height(180.dp)
                    .padding(top = 12.dp)
                    .fillMaxWidth(), update = {
                    val lineDataSet = LineDataSet(budget.graphEntries, "")
                    lineDataSet.setDrawFilled(true)
                    lineDataSet.color = budget.color
                    lineDataSet.valueTextColor =  Color.Unspecified.toArgb()
                    lineDataSet.fillDrawable = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        intArrayOf(Color(budget.color).copy(alpha = 0.2f).toArgb(), budget.color)
                    )
                    val data = LineData(lineDataSet)
                    it.data = data
                    it.xAxis.setDrawGridLines(false)
                    it.setScaleEnabled(false)
                    it.description.isEnabled = false
                    it.marker = mv
                    it.invalidate()
                })
            }
        }
    }
}