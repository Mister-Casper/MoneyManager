package com.sgcdeveloper.moneymanager.presentation.ui.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.AddNewWallet
import com.sgcdeveloper.moneymanager.domain.model.AddRecurringTransaction
import com.sgcdeveloper.moneymanager.domain.model.BaseBudget
import com.sgcdeveloper.moneymanager.domain.model.RecurringTransaction
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.ui.composables.BudgetDashboard
import com.sgcdeveloper.moneymanager.presentation.ui.composables.RecurringTransactionsDashboard
import com.sgcdeveloper.moneymanager.presentation.ui.composables.WalletDashboard

@Composable
fun HomeScreen(homeViewModel: HomeViewModel, navController: NavController) {
    val state = remember { homeViewModel.state }.value

    Column(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.dashboard),
                Modifier.align(Alignment.CenterStart).padding(start = 12.dp),
                color = MaterialTheme.colors.onBackground,
                fontSize = 24.sp,
            )
            Row(Modifier.align(Alignment.CenterEnd).padding(end = 48.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.calculator_icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { navController.navigate(Screen.Calculators.route) }
                )
            }
            Row(Modifier.align(Alignment.CenterEnd).padding(end = 12.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { navController.navigate(Screen.Settings.route) }
                )
            }
        }
        LazyColumn(
            Modifier
                .padding(bottom = 56.dp,start = 12.dp, end = 12.dp)
        ) {
            item {
                WalletDashboard(state.wallets, {
                    if (it is AddNewWallet) {
                        navController.navigate(Screen.AddWallet(it).route)
                    } else {
                        navController.navigate(Screen.WalletScreen(it).route)
                    }
                }, {
                    navController.navigate(Screen.WalletsManagerScreen.route)
                })
            }
            item {
                BudgetDashboard(state.budgets, {
                    if (it is BaseBudget.AddNewBudget) {
                        navController.navigate(Screen.AddBudgetScreen().route)
                    } else if (it is BaseBudget.BudgetItem) {
                        navController.navigate(Screen.BudgetScreen(it).route)
                    }
                }) {
                    navController.navigate(Screen.BudgetManagerScreen.route)
                }
            }
            item {
                AndroidView(
                    modifier = Modifier.fillMaxWidth(),
                    factory = { context ->
                        AdView(context).apply {
                            adSize = AdSize.LARGE_BANNER
                            adUnitId = "ca-app-pub-5494709043617393/2510789678"
                            loadAd(AdRequest.Builder().build())
                        }
                    }
                )
                RecurringTransactionsDashboard(state.recurringTransactions) {
                    if (it is RecurringTransaction) {
                        navController.navigate(Screen.AddRecurringTransaction(it).route)
                    } else if (it is AddRecurringTransaction) {
                        navController.navigate(Screen.AddRecurringTransaction().route)
                    }
                }
            }
        }
    }
}