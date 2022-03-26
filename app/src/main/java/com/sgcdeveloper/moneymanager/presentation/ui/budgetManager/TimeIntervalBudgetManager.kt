package com.sgcdeveloper.moneymanager.presentation.ui.budgetManager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.ui.composables.TimeIntervalControllerView

@Composable
fun TimeIntervalBudgetManager(
    navController: NavController,
    timeIntervalBudgetManagerViewModel: TimeIntervalBudgetManagerViewModel
) {
    val budgets = remember { timeIntervalBudgetManagerViewModel.budgets }
    LazyColumn(Modifier.padding(12.dp)) {
        item {
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
                        text = stringResource(id = timeIntervalBudgetManagerViewModel.period.mediumNameRes),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp)
                            .weight(1f)
                    )
                }
            }
            Row(Modifier.fillMaxWidth()) {
                TimeIntervalControllerView({
                    timeIntervalBudgetManagerViewModel.moveBack()
                }, {
                    timeIntervalBudgetManagerViewModel.moveNext()
                }, true, timeIntervalBudgetManagerViewModel.description.value)
            }
        }
        items(budgets.size) {
            val budgetItem = budgets[it]
            BudgetItem(budgetItem) {
                navController.navigate(Screen.BudgetScreen(budgetItem).route)
            }
        }
    }
}