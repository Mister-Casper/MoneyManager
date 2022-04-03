package com.sgcdeveloper.moneymanager.presentation.ui.budgetManager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(32.dp)
                        .padding(start = 12.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
                androidx.compose.material3.Text(
                    text = stringResource(id = timeIntervalBudgetManagerViewModel.period.mediumNameRes),
                    fontSize = 24.sp,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 12.dp)
                )
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