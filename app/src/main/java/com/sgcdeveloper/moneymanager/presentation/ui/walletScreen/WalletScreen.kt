package com.sgcdeveloper.moneymanager.presentation.ui.walletScreen

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.WalletCard
import com.sgcdeveloper.moneymanager.util.TimeInternalSingleton

@Composable
fun WalletScreen(walletViewModel: WalletViewModel, navController: NavController) {
    val allText = LocalContext.current.getString(R.string.all)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(top = 12.dp, bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(32.dp)
                        .clickable { navController.popBackStack() },
                    tint = MaterialTheme.colors.onBackground
                )
                Row(Modifier.align(Alignment.CenterEnd)) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_icon),
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(40.dp)
                            .clickable { navController.navigate(Screen.AddWallet(walletViewModel.wallet).route) },
                        tint = MaterialTheme.colors.onBackground
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.statistic_icon),
                        contentDescription = "",
                        tint = MaterialTheme.colors.onBackground,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(40.dp)
                            .clickable {
                                navController.navigate(
                                    Screen.TransactionCategoryForWalletStatisticScreen(
                                        walletViewModel.wallet
                                    ).route
                                )
                            }
                    )
                }
            }
            WalletCard(walletViewModel.wallet, {})
            LazyColumn(Modifier.fillMaxSize()) {
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
                                fontSize = 18.sp
                            )
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.income),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = walletViewModel.income.value
                                )
                            }
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.expense),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(text = walletViewModel.expense.value, color = red)
                            }
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.transfer),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = walletViewModel.transfers.value
                                )
                            }
                        }
                    }
                }
                items(walletViewModel.transactionsStatistic.value.size) {
                    val item = walletViewModel.transactionsStatistic.value[it]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                TimeInternalSingleton.timeIntervalController =
                                    TimeIntervalController.AllController(allText)
                                navController.navigate(
                                    Screen.TransactionCategoryTransactions(
                                        walletViewModel.wallet,
                                        item.categoryEntry
                                    ).route
                                )
                            }
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
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
                                Text(
                                    text = item.category,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = white
                                )
                                Text(
                                    text = item.count,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = white
                                )
                            }
                            Text(
                                text = item.money,
                                modifier = Modifier.align(Alignment.CenterVertically),
                                color = Color(item.moneyColor)
                            )
                        }
                    }
                }
            }
        }
    }
}