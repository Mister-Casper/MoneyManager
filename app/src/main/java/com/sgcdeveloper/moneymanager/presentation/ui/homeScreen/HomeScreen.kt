package com.sgcdeveloper.moneymanager.presentation.ui.homeScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.ui.composables.WalletDashboard

@Composable
fun HomeScreen(homeViewModel: HomeViewModel, navController: NavController) {
    val wallets = homeViewModel.wallets.observeAsState()

    LazyColumn(Modifier.padding(bottom = 60.dp)) {
        item {
            if (wallets.value != null)
                WalletDashboard(wallets.value!!) {
                    navController.navigate(Screen.AddWallet(it).route)
                }
        }
    }
}