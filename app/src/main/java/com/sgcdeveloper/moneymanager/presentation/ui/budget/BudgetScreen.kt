package com.sgcdeveloper.moneymanager.presentation.ui.budget

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BaseBudget
import com.sgcdeveloper.moneymanager.domain.model.CategoryStatistic
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.dark_gray
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.RoundedLinearProgressIndicator
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DeleteDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.util.BudgetMarkerView
import com.sgcdeveloper.moneymanager.util.WalletSingleton

@Composable
fun BudgetScreen(
    budgetScreenViewModel: BudgetScreenViewModel,
    budget: BaseBudget.BudgetItem,
    navController: NavController
) {
    val dialog = budgetScreenViewModel.dialogState.value

    if (dialog is DialogState.DeleteDialog) {
        DeleteDialog(dialog.massage, {
            budgetScreenViewModel.deleteBudget(budget)
            budgetScreenViewModel.closeDialog()
            navController.popBackStack()
        }, {
            budgetScreenViewModel.closeDialog()
        })
    }

    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            Row(Modifier.align(Alignment.CenterStart)) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "",
                    tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 12.dp)
                        .size(20.dp)
                        .clickable { navController.popBackStack() }
                )
                Text(
                    text = stringResource(id = R.string.budget_view),
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 12.dp)
                        .weight(1f)
                )
            }
            Row(Modifier.align(Alignment.CenterEnd).padding(end = 12.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_icon),
                    contentDescription = "",
                    tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(32.dp)
                        .clickable { navController.navigate(Screen.AddBudgetScreen(budget.budgetEntry).route) }
                )
                Icon(
                    painter = painterResource(id = R.drawable.delete_icon),
                    contentDescription = "",
                    tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(32.dp)
                        .clickable { budgetScreenViewModel.showDeleteBudgetDialog() })
            }
        }
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            item {
                Text(text = budget.budgetName, fontSize = 24.sp)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.spent),
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        text = stringResource(id = budget.leftStrRes, ""),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
                Box(Modifier.fillMaxWidth()) {
                    Text(
                        text = budget.spent,
                        Modifier.align(Alignment.CenterStart),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = budget.left,
                        Modifier.align(Alignment.CenterEnd),
                        fontWeight = FontWeight.Bold
                    )
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
                            modifier = Modifier.weight(0.3f)
                        )
                        Text(
                            text = budget.categoryDescription,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(0.7f)
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
                        lineDataSet.valueTextColor = Color.Unspecified.toArgb()
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
                        val ll = LimitLine(budget.budgetValue.toFloat(), budget.budget)
                        ll.lineWidth = 2f
                        ll.enableDashedLine(30f, 8f, 0f)
                        ll.lineColor = red.toArgb()
                        ll.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                        ll.textSize = 10f
                        ll.typeface = Typeface.DEFAULT
                        ll.textColor = textColor.toArgb()
                        this.axisLeft.addLimitLine(ll)
                        this.axisLeft.axisMaximum = budget.maxX.toFloat()
                    }
                }, modifier = Modifier
                    .height(180.dp)
                    .padding(top = 12.dp)
                    .fillMaxWidth(), update = {
                    val lineDataSet = LineDataSet(budget.graphEntries, "")
                    lineDataSet.setDrawFilled(true)
                    lineDataSet.color = budget.color
                    lineDataSet.valueTextColor = Color.Unspecified.toArgb()
                    lineDataSet.fillDrawable = GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        intArrayOf(Color(budget.color).copy(alpha = 0.2f).toArgb(), budget.color)
                    )
                    val data = LineData(lineDataSet)
                    it.data = data
                    it.xAxis.setDrawGridLines(false)
                    it.setScaleEnabled(false)
                    it.description.isEnabled = false
                    val ll = LimitLine(budget.budgetValue.toFloat(), budget.budget)
                    ll.lineWidth = 2f
                    ll.enableDashedLine(30f, 8f, 0f)
                    ll.lineColor = red.toArgb()
                    ll.labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                    ll.textSize = 10f
                    ll.typeface = Typeface.DEFAULT
                    ll.textColor = textColor.toArgb()
                    it.axisLeft.addLimitLine(ll)
                    it.axisLeft.axisMaximum = budget.maxX.toFloat()
                    it.marker = mv
                    it.invalidate()
                })

                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = budget.startPeriod,
                        modifier = Modifier.align(Alignment.CenterStart),
                        color = MaterialTheme.colors.secondary
                    )
                    Text(
                        text = budget.endPeriod,
                        modifier = Modifier.align(Alignment.CenterEnd),
                        color = MaterialTheme.colors.secondary
                    )
                }
                Text(
                    text = stringResource(id = R.string.transaction_list),
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            items(budget.spendCategories.size) {
                val category = budget.spendCategories[it]
                TransactionCategoryItem(category, navController)
            }
        }
    }
}

@Composable
fun TransactionCategoryItem(
    item: CategoryStatistic,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
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
                        androidx.compose.material.Icon(
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