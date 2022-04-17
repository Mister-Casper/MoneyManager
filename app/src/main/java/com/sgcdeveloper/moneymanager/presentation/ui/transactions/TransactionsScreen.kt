package com.sgcdeveloper.moneymanager.presentation.ui.transactions

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
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
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DeleteWalletDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.WalletPickerDialog
import com.sgcdeveloper.moneymanager.util.WalletSingleton


@Composable
fun TransactionsScreen(transactionsViewModel: TransactionsViewModel, navController: NavController) {
    val state = remember { transactionsViewModel.state }.value

    if (state.dialogState is DialogState.WalletPickerDialog) {
        WalletPickerDialog(state.wallets, state.wallet, {
            transactionsViewModel.onEvent(TransactionEvent.ChangeWallet(it))
        }, {
            transactionsViewModel.onEvent(TransactionEvent.CloseDialog)
        }, {
            navController.navigate(Screen.AddWallet(it).route)
        })
    } else if (state.dialogState is DialogState.DeleteTransactionDialog) {
        DeleteWalletDialog(
            null,
            {
                transactionsViewModel.onEvent(TransactionEvent.DeleteSelectedTransactions)
                transactionsViewModel.onEvent(TransactionEvent.ChangeSelectionMode)
                transactionsViewModel.onEvent(TransactionEvent.CloseDialog)
            },
            {
                transactionsViewModel.onEvent(TransactionEvent.CloseDialog)
            },
            title = stringResource(
                id = R.string.are_u_sure_delete_selected_transaction,
                transactionsViewModel.state.value.selectedCount
            )
        )
    }

    if (state.isShareSelectedTransactions) {
        val transactionsText = transactionsViewModel.getSelectedTransactionsText()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_TEXT, transactionsText)
        LocalContext.current.startActivity(Intent.createChooser(intent, stringResource(id = R.string.transactions)))

        transactionsViewModel.state.value = transactionsViewModel.state.value.copy(isShareSelectedTransactions = false, isMultiSelectionMode = false)
    }

    state.wallet?.let {
        CheckDataFromAddTransactionScreen(navController, transactionsViewModel, state.wallet.walletId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            if (!state.isMultiSelectionMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.surface)
                        .padding(top = 16.dp, bottom = 16.dp)
                ) {
                    Row(
                        Modifier
                            .align(Alignment.CenterStart)
                            .clickable { transactionsViewModel.onEvent(TransactionEvent.ShowWalletPickerDialog) }) {
                        state.wallet?.let {
                            Text(
                                text = state.wallet.name,
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 12.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            "",
                            Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    Row(
                        Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                    ) {
                        var expandedMenu by remember { mutableStateOf(false) }
                        Box(Modifier.align(Alignment.CenterVertically)) {
                            Icon(
                                painter = painterResource(id = R.drawable.dots_icon),
                                contentDescription = "Show menu",
                                Modifier
                                    .size(32.dp)
                                    .clickable { expandedMenu = true }
                            )
                            DropdownMenu(
                                expanded = expandedMenu,
                                onDismissRequest = { expandedMenu = false }
                            ) {
                                DropdownMenuItem(onClick = {
                                    expandedMenu = false
                                    navController.navigate(Screen.TransactionsCalendarScreen.route)
                                }) {
                                    Text(stringResource(id = R.string.calendar_menu))
                                }
                                DropdownMenuItem(onClick = {
                                    expandedMenu = false
                                    navController.navigate(Screen.SearchTransactionsScreen.route)
                                }) {
                                    Text(stringResource(id = R.string.search_menu))
                                }
                                DropdownMenuItem(onClick = {
                                    expandedMenu = false
                                    if (state.wallet != null)
                                        navController.navigate(
                                            Screen.TimeIntervalTransactions(
                                                state.wallet
                                            ).route
                                        )
                                }) {
                                    Text(stringResource(id = R.string.time_range_transactions))
                                }
                                Divider()
                                DropdownMenuItem(onClick = {
                                    expandedMenu = false
                                    navController.navigate(Screen.Settings.route)
                                }) {
                                    Text(stringResource(id = R.string.settings))
                                }
                            }
                        }
                    }
                }
                Row(Modifier.padding(top = 4.dp, start = 12.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.balance_icon),
                        contentDescription = "",
                        Modifier
                            .size(32.dp)
                            .align(Alignment.CenterVertically)
                    )
                    state.wallet?.let {
                        Text(
                            text = stringResource(id = R.string.balance, state.wallet.formattedMoney),
                            Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 8.dp),
                            fontSize = 18.sp
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.surface)
                        .padding(top = 15.dp, bottom = 14.dp, start = 16.dp, end = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.cancel_icon),
                        "",
                        Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .clickable {
                                transactionsViewModel.onEvent(TransactionEvent.ChangeSelectionMode)
                            }
                    )
                    Text(
                        text = state.selectedCount,
                        fontSize = 26.sp,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    )
                    Icon(
                        imageVector = Icons.Filled.Share,
                        "",
                        Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .clickable {
                                transactionsViewModel.onEvent(TransactionEvent.ShareSelectedTransactions)
                            }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        "",
                        Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .clickable {
                                transactionsViewModel.onEvent(TransactionEvent.ShowDeleteSelectedTransactionsDialog)
                            }
                    )
                    var expanded by remember { mutableStateOf(false) }
                    Box(Modifier.align(Alignment.CenterVertically)) {
                        Icon(
                            painter = painterResource(id = R.drawable.dots_icon),
                            contentDescription = "Show menu",
                            Modifier
                                .size(32.dp)
                                .clickable { expanded = true }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(onClick = {
                                expanded = false
                                transactionsViewModel.onEvent(TransactionEvent.SelectAll)
                            }) {
                                Text(stringResource(id = R.string.select_all))
                            }
                            DropdownMenuItem(onClick = {
                                expanded = false
                                transactionsViewModel.onEvent(TransactionEvent.ClearAll)
                            }) {
                                Text(stringResource(id = R.string.clear_selection))
                            }
                        }
                    }
                }
            }
            LazyColumn(Modifier.padding(start = 12.dp, end = 12.dp)) {
                items(state.transactions.size) {
                    val transactionItem = state.transactions[it]
                    if (transactionItem is BaseTransactionItem.TransactionHeader) {
                        TransactionHeader(transactionItem)
                    } else if (transactionItem is BaseTransactionItem.TransactionItem) {
                        TransactionItem(transactionItem, navController, state.isMultiSelectionMode, {
                            transactionsViewModel.onEvent(TransactionEvent.ChangeSelectionItemMode(it))
                        }) {
                            transactionsViewModel.onEvent(TransactionEvent.ChangeSelectionMode)
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(bottom = 58.dp))
                }
            }
        }

        if (state.isEmpty) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(Modifier.align(Alignment.Center)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(id = R.drawable.empty_icon),
                            contentDescription = "",
                            Modifier
                                .align(Alignment.CenterVertically)
                                .weight(2f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Text(
                        text = stringResource(id = R.string.no_transactions),
                        style = Typography.h5,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = stringResource(id = R.string.tap_to_add_transaction),
                        fontWeight = FontWeight.Thin,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        OutlinedButton(
            onClick = { if (state.wallet != null) navController.navigate(Screen.AddTransaction(state.wallet).route) },
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

    BackHandler {

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
                fontWeight = FontWeight.Bold
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = header.dayName, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = header.month, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Text(text = header.money, Modifier.align(Alignment.CenterVertically))
        }
    }
    Divider(color = MaterialTheme.colors.background, thickness = 2.dp)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionItem(
    item: BaseTransactionItem.TransactionItem,
    navController: NavController,
    isMultiSelection: Boolean,
    onChangedSelection: (id: Long) -> Unit,
    changedSelectionMode: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isMultiSelection) onChangedSelection(item.transactionEntry.id)
                    else navController.navigate(Screen.EditTransaction(transaction = item.transactionEntry).route)
                },
                onLongClick = {
                    if (!isMultiSelection) onChangedSelection(item.transactionEntry.id)
                    changedSelectionMode()
                },
            )
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
                Text(text = item.category, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(text = item.description, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            Text(
                text = item.money,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically),
                color = if (item.moneyColor != Color.Unspecified.toArgb()) Color(item.moneyColor) else MaterialTheme.colors.onBackground
            )
            if (isMultiSelection) {
                Checkbox(checked = item.isSelection, onCheckedChange = { onChangedSelection(item.transactionEntry.id) })
            }
        }
    }
}

@Composable
fun CheckDataFromAddTransactionScreen(
    navController: NavController,
    transactionsViewModel: TransactionsViewModel,
    walletId: Long
) {
    val secondScreenResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<Long>("wallet_id")

    LaunchedEffect(Unit) {
        secondScreenResult?.let {
            if (walletId == 0L) {
                transactionsViewModel.loadTransactions(WalletSingleton.wallet.value!!)
            } else if (secondScreenResult != -1L) {
                transactionsViewModel.onEvent(TransactionEvent.ChangeWalletById(it))
            }
        }
    }
}