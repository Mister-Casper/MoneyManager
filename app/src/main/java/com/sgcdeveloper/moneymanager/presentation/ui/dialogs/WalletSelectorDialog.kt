package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.AllExpense
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.presentation.theme.white

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WalletSelectorDialog(
    wallets: List<Wallet>,
    defaultWallets: List<Wallet>,
    onAdd: (wallets: List<Wallet>) -> Unit,
    onDismiss: () -> Unit
) {
    var items = if (defaultWallets.containsAll(wallets))
        wallets
    else
        defaultWallets
    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onDismiss,
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .clickable { onDismiss() }
                    )
                }
                Button(onClick = {
                    onAdd(items.sortedBy { it.walletId })
                    onDismiss()
                }, Modifier.align(Alignment.CenterEnd)) {
                    Text(
                        text = stringResource(id = R.string.save),
                        Modifier.align(Alignment.CenterVertically),
                        color = white
                    )
                }
            }
        },
        text = {
            WalletSelector(wallets,defaultWallets) {
                items = it
            }
        },
        confirmButton = {})
}

@Composable
private fun WalletSelector(
    items:List<Wallet>,
    default: List<Wallet>,
    onAdd: (category: List<Wallet>) -> Unit,
) {
    val context = LocalContext.current
    val selectedOption = rememberMutableStateListOf<Int>()
    selectedOption.addAll((default.map { it.walletId.toInt() }))

    Column(Modifier.fillMaxWidth()) {
        LazyColumn {
            items(items.size) {
                val item = items[it]
                Row(
                    Modifier
                        .padding(4.dp)
                        .clickable {
                            if (item.walletId == AllExpense(context).id) {
                                if (item.walletId.toInt() in selectedOption) {
                                    selectedOption.clear()
                                } else {
                                    selectedOption.addAll(items.map { it.walletId.toInt() })
                                }
                            } else {
                                selectedOption.remove(AllExpense(context).id.toInt())
                                if (item.walletId.toInt() in selectedOption) {
                                    selectedOption.removeAll { it == item.walletId.toInt() }
                                } else {
                                    selectedOption.add(item.walletId.toInt())
                                }
                            }
                            onAdd(
                                selectedOption
                                    .toSet()
                                    .toList()
                                    .map { items.find {item->item.walletId == it.toLong()} as Wallet }
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(4.dp)
                            .align(Alignment.CenterVertically),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Box(modifier = Modifier.background(Color(item.color))) {
                            androidx.compose.material.Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = "",
                                Modifier
                                    .align(Alignment.Center)
                                    .size(32.dp),
                                tint = white
                            )
                        }
                    }
                    Text(
                        text =  item.name,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .weight(1f),
                        fontSize = 18.sp
                    )
                    RadioButton(
                        selected = (selectedOption.contains(item.walletId.toInt())),
                        onClick = null
                    )
                }
            }
        }
    }
}