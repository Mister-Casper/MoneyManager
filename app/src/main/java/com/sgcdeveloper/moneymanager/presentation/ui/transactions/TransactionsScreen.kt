package com.sgcdeveloper.moneymanager.presentation.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BaseTransactionItem
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.WalletPickerDialog

@Composable
fun TransactionsScreen(transactionsViewModel: TransactionsViewModel, navController: NavController) {
    val wallet = transactionsViewModel.defaultWallet.observeAsState()
    val transactions = transactionsViewModel.transactionItems.observeAsState()
    val dialog = remember { transactionsViewModel.dialog }

    if (dialog.value is DialogState.WalletPickerDialog) {
        WalletPickerDialog(transactionsViewModel.wallets.value, transactionsViewModel.defaultWallet.value, {
            transactionsViewModel.onEvent(TransactionEvent.ChangeWallet(it))
        }, {
            transactionsViewModel.onEvent(TransactionEvent.CloseDialog)
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
    ) {
        LazyColumn(Modifier.padding(12.dp)) {
            item {
                Column(Modifier.fillMaxSize()) {
                    Row(Modifier.clickable { transactionsViewModel.onEvent(TransactionEvent.ShowWalletPickerDialog) }) {
                        wallet.value?.let {
                            Text(
                                text = wallet.value!!.name,
                                fontSize = 22.sp,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                "",
                                Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                    Row(Modifier.padding(top = 4.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.balance_icon),
                            contentDescription = "",
                            Modifier
                                .size(32.dp)
                                .align(Alignment.CenterVertically)
                        )
                        wallet.value?.let {
                            Text(
                                text = stringResource(id = R.string.balance, wallet.value!!.formattedMoney),
                                Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 8.dp),
                                fontSize = 18.sp
                            )
                        }
                    }
                    Divider(
                        color = MaterialTheme.colors.secondary,
                        thickness = 1.dp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
            transactions.value?.let {
                items(transactions.value!!.size) {
                    val transactionItem = transactions.value!![it]
                    if(transactionItem is BaseTransactionItem.TransactionHeader){
                        TransactionHeader(transactionItem)
                    }else if(transactionItem is BaseTransactionItem.TransactionItem){
                        TransactionItem(transactionItem)
                    }
                }
            }
        }

        OutlinedButton(
            onClick = { navController.navigate(Screen.AddTransaction(transactionsViewModel.defaultWallet.value).route) },
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
fun TransactionHeader(header:BaseTransactionItem.TransactionHeader){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
    ) {
        Row(Modifier.fillMaxWidth().padding(6.dp)) {
            Text(text = header.dayNum, fontSize = 42.sp, modifier = Modifier.align(Alignment.CenterVertically), fontWeight = FontWeight.Bold, color = white)
            Column(Modifier.weight(1f).padding(start = 8.dp).align(Alignment.CenterVertically)) {
                Text(text = header.dayName, fontSize = 14.sp, fontWeight = FontWeight.Thin, color = white)
                Text(text = header.month, fontSize = 12.sp, fontWeight = FontWeight.Thin, color = white)
            }
            Text(text = header.money,Modifier.align(Alignment.CenterVertically), color = white)
        }
    }
}

@Composable
fun TransactionItem(item:BaseTransactionItem.TransactionItem){

}