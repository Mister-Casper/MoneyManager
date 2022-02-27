package com.sgcdeveloper.moneymanager.presentation.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.Typography
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.WalletPickerDialog
import com.sgcdeveloper.moneymanager.util.WalletSingleton

@Composable
fun TransactionsScreen(transactionsViewModel: TransactionsViewModel, navController: NavController) {
    val wallet = remember { WalletSingleton.wallet }.observeAsState()
    val transactions = remember { transactionsViewModel.transactionItems }
    val dialog = remember { transactionsViewModel.dialog }

    if (dialog.value is DialogState.WalletPickerDialog) {
        WalletPickerDialog(transactionsViewModel.wallets.value, wallet.value, {
            transactionsViewModel.onEvent(TransactionEvent.ChangeWallet(it))
        }, {
            transactionsViewModel.onEvent(TransactionEvent.CloseDialog)
        })
    }

    CheckDataFromAddTransactionScreen(navController, transactionsViewModel)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp)
            .padding(start = 4.dp, top = 4.dp, end = 4.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .align(Alignment.CenterStart)
                        .clickable {
                            transactionsViewModel.onEvent(TransactionEvent.ShowWalletPickerDialog)
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("wallet_id", -1L)
                        }) {
                    WalletSingleton.wallet.value?.let {
                        Text(
                            text = WalletSingleton.wallet.value!!.name,
                            fontSize = 22.sp,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            color = MaterialTheme.colors.secondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        "",
                        Modifier.align(Alignment.CenterVertically),
                        tint = MaterialTheme.colors.secondary
                    )
                }
                Row(Modifier.align(Alignment.CenterEnd)) {
                    Icon(
                        painter = painterResource(id = R.drawable.list_icon),
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .clickable {
                                navController.navigate(
                                    Screen.TimeIntervalTransactions(
                                        wallet.value
                                    ).route
                                )
                            }
                    )
                    Spacer(modifier = Modifier.padding(start = 12.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.settings_icon),
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .clickable { navController.navigate(Screen.Settings.route) }
                    )
                }
            }
            Row(Modifier.padding(top = 4.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.balance_icon),
                    contentDescription = "",
                    Modifier
                        .size(32.dp)
                        .align(Alignment.CenterVertically),
                    tint = MaterialTheme.colors.secondary
                )
                wallet.value?.let {
                    Text(
                        text = stringResource(id = R.string.balance, wallet.value!!.formattedMoney),
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        fontSize = 18.sp,
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
            Divider(
                color = MaterialTheme.colors.secondary,
                thickness = 1.dp,
                modifier = Modifier.padding(top = 16.dp)
            )
            LazyColumn(Modifier.padding(start = 12.dp, end = 12.dp)) {
                items(transactions.value.size) {
                    val transactionItem = transactions.value[it]
                    if (transactionItem is BaseTransactionItem.TransactionHeader) {
                        TransactionHeader(transactionItem)
                    } else if (transactionItem is BaseTransactionItem.TransactionItem) {
                        TransactionItem(transactionItem, navController)
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(bottom = 55.dp))
                }
            }
        }

        if (transactionsViewModel.isEmpty.value) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(Modifier.align(Alignment.Center)) {
                    Icon(
                        painter = painterResource(id = R.drawable.empty_icon),
                        contentDescription = "",
                        Modifier.align(Alignment.CenterHorizontally),
                        tint = MaterialTheme.colors.secondary
                    )
                    Text(
                        text = stringResource(id = R.string.no_transactions),
                        style = Typography.h5,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colors.secondary
                    )
                    Text(
                        text = stringResource(id = R.string.tap_to_add_transaction),
                        fontWeight = FontWeight.Thin,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
        }

        OutlinedButton(
            onClick = { navController.navigate(Screen.AddTransaction(wallet.value).route) },
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 8.dp, end = 8.dp)
                .align(Alignment.BottomEnd),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = blue)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add_icon),
                contentDescription = "",
                tint = white,
                modifier = Modifier.size(1000.dp)
            )
        }
    }
}

@Composable
fun TransactionHeader(header: BaseTransactionItem.TransactionHeader) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Text(
                text = header.dayNum,
                fontSize = 42.sp,
                modifier = Modifier.align(Alignment.CenterVertically),
                fontWeight = FontWeight.Bold,
                color = white
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = header.dayName, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = white)
                Text(text = header.month, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = white)
            }
            Text(text = header.money, Modifier.align(Alignment.CenterVertically), color = white)
        }
    }
    Divider(color = MaterialTheme.colors.background, thickness = 2.dp)
}

@Composable
fun TransactionItem(item: BaseTransactionItem.TransactionItem, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.EditTransaction(transaction = item.transactionEntry).route) }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {
            Card(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterVertically),
                shape = RoundedCornerShape(8.dp),
            ) {
                Box(modifier = Modifier.background(Color(item.color))) {
                    Icon(
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
                Text(text = item.category, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = white)
                Text(text = item.description, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = white)
            }
            Text(
                text = item.money,
                modifier = Modifier.align(Alignment.CenterVertically),
                color = Color(item.moneyColor)
            )
        }
    }
}

@Composable
fun CheckDataFromAddTransactionScreen(navController: NavController, transactionsViewModel: TransactionsViewModel) {
    val secondScreenResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<Long>("wallet_id")

    secondScreenResult?.let {
        if (secondScreenResult != -1L) {
            transactionsViewModel.onEvent(TransactionEvent.ChangeWalletById(it))
        }
    }
}