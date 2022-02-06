package com.sgcdeveloper.moneymanager.presentation.ui.homeScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.ui.composables.WalletCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel, navController: NavController) {
    val wallets = homeViewModel.wallets.observeAsState()

    Column(Modifier.fillMaxSize()) {
        Card(
            Modifier
                .fillMaxWidth()
                .padding(8.dp), shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.wallet),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 12.dp),
                    color = MaterialTheme.colors.secondary
                )
                LazyVerticalGrid(
                    cells = GridCells.Adaptive(120.dp)
                ) {
                    if (wallets.value != null) {
                        items(wallets.value!!.size) {
                            val wallet = wallets.value!![it]
                            WalletCard(wallet) {
                                navController.navigate(Screen.AddWallet(it).route)
                            }
                        }
                    }
                }
            }
        }
    }
}