package com.sgcdeveloper.moneymanager.presentation.ui.homeScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.AddNewWallet
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.ui.composables.WalletDashboard

@Composable
fun HomeScreen(homeViewModel: HomeViewModel, navController: NavController) {
    val wallets = homeViewModel.wallets.observeAsState()

    LazyColumn(
        Modifier
            .padding(bottom = 60.dp)
            .padding(start = 4.dp, top = 4.dp, end = 4.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.dashboard),
                    Modifier.align(Alignment.CenterStart),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                )
                Row(Modifier.align(Alignment.CenterEnd)) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings_icon),
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { navController.navigate(Screen.Settings.route) }
                    )
                }
            }
        }
        item {
            if (wallets.value != null)
                WalletDashboard(wallets.value!!) {
                    if(it is AddNewWallet){
                        navController.navigate(Screen.AddWallet(it).route)
                    }else{
                        navController.navigate(Screen.WalletScreen(it).route)
                    }
                }
        }
    }
}