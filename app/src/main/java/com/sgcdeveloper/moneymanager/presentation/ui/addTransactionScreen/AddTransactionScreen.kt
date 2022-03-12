package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.gray
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.*

@Composable
fun AddTransactionScreen(addTransactionViewModel: AddTransactionViewModel, navController: NavController) {
    val currentTransactionScreen = remember { addTransactionViewModel.currentScreen }
    val currentScreenName = remember { addTransactionViewModel.currentScreenName }
    val dialog = remember { addTransactionViewModel.dialogState }
    val dialogBackOpen = remember { addTransactionViewModel.backDialog }
    val signalBack = remember { addTransactionViewModel.back }
    val wallets = addTransactionViewModel.wallets.observeAsState()

    if (dialog.value is DialogState.DatePickerDialog) {
        DatePicker(
            defaultDate = addTransactionViewModel.transactionDate.value,
            onDateSelected = { addTransactionViewModel.onEvent(AddTransactionEvent.ChangeTransactionDate(it)) },
            onDismissRequest = {
                addTransactionViewModel.onEvent(AddTransactionEvent.CloseDialog)
            }, addTransactionViewModel.isDarkTheme()
        )
    } else if (dialog.value is DialogState.CategoryPickerDialog) {
        SelectTransactionCategoryDialog(isIncome = addTransactionViewModel.currentScreen.value == TransactionScreen.Income,
            defaultCategory = addTransactionViewModel.getDefaultTransactionCategory(),
            onAdd = {
                addTransactionViewModel.onEvent(AddTransactionEvent.ChangeTransactionCategory(it))
            },
            onDismiss = { addTransactionViewModel.onEvent(AddTransactionEvent.CloseDialog) })
    } else if (dialog.value is DialogState.WalletPickerDialog) {
        WalletPickerDialog(
            wallets = wallets.value,
            (dialog.value as DialogState.WalletPickerDialog).wallet,
            {
                addTransactionViewModel.onEvent(AddTransactionEvent.ChangeTransactionWallet(it))
            }, {
                addTransactionViewModel.onEvent(AddTransactionEvent.CloseDialog)
            }, {
                navController.navigate(Screen.AddWallet(it).route)
            })
    } else if (dialog.value is DialogState.DeleteTransactionDialog) {
        DeleteWalletDialog(null, {
            addTransactionViewModel.onEvent(AddTransactionEvent.DeleteTransaction)
            addTransactionViewModel.clear()
            navController.popBackStack()
        }, {
            addTransactionViewModel.onEvent(AddTransactionEvent.CloseDialog)
        }, R.string.are_u_sure_delete_transaction)
    }
    if (dialogBackOpen.value){
        DialogBack(dialogBackOpen.value,signalBack.value,
            signalReturn={
                signalBack.value = false
                dialogBackOpen.value = false
            },
            dialogOpen={
                dialogBackOpen.value = false
                addTransactionViewModel.clear()
                navController.popBackStack()
            }
        )
    }

    when (addTransactionViewModel.currentScreen.value) {
        TransactionScreen.Expense -> addTransactionViewModel.currentScreenName.value =
            stringResource(id = R.string.expense)
        TransactionScreen.Income -> addTransactionViewModel.currentScreenName.value =
            stringResource(id = R.string.income)
        else -> addTransactionViewModel.currentScreenName.value = stringResource(id = R.string.transfer)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "",
                tint = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(40.dp)
                    .clickable {
                        dialogBackOpen.value = true
                    }
            )
            Text(
                text = currentScreenName.value,
                color = MaterialTheme.colors.secondary,
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp)
                    .weight(1f)
            )
            if (addTransactionViewModel.transactionId != 0L) {
                Icon(
                    painter = painterResource(id = R.drawable.delete_icon),
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 4.dp)
                        .size(48.dp)
                        .clickable {
                            addTransactionViewModel.onEvent(AddTransactionEvent.ShowDeleteTransactionDialog)
                        }
                )
            }
            Button(
                onClick = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("wallet_id", addTransactionViewModel.transactionFromWallet.value!!.walletId)
                    navController.popBackStack()
                    addTransactionViewModel.onEvent(AddTransactionEvent.InsertTransaction)
                }, enabled = addTransactionViewModel.isTransactionCanBeSaved.value,
                colors = ButtonDefaults.buttonColors(disabledBackgroundColor = gray)
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    Modifier.align(Alignment.CenterVertically),
                    color = if (addTransactionViewModel.isTransactionCanBeSaved.value) white else MaterialTheme.colors.secondary
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            transactionNavigationButton(
                { addTransactionViewModel.onEvent(AddTransactionEvent.ChangeAddTransactionScreen(it)) },
                currentTransactionScreen.value,
                TransactionScreen.Income,
                stringResource(id = R.string.income)
            )
            transactionNavigationButton(
                { addTransactionViewModel.onEvent(AddTransactionEvent.ChangeAddTransactionScreen(it)) },
                currentTransactionScreen.value,
                TransactionScreen.Expense,
                stringResource(id = R.string.expense)
            )
            transactionNavigationButton(
                { addTransactionViewModel.onEvent(AddTransactionEvent.ChangeAddTransactionScreen(it)) },
                currentTransactionScreen.value,
                TransactionScreen.Transfer,
                stringResource(id = R.string.transfer)
            )
        }
        when (currentTransactionScreen.value) {
            TransactionScreen.Income -> {
                AddIncomeScreen(addTransactionViewModel)
            }
            TransactionScreen.Expense -> {
                AddExpenseScreen(addTransactionViewModel)
            }
            TransactionScreen.Transfer -> {
                AddTransferScreen(addTransactionViewModel)
            }
        }
    }

    BackHandler {
        dialogBackOpen.value = true
    }
}


@Composable
fun RowScope.transactionNavigationButton(
    onClick: (targetScreen: TransactionScreen) -> Unit,
    currentTransactionScreen: TransactionScreen,
    targetScreen: TransactionScreen,
    text: String
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (currentTransactionScreen == targetScreen) blue else MaterialTheme.colors.background,
            contentColor = if (currentTransactionScreen == targetScreen) white else MaterialTheme.colors.secondary
        ),
        onClick = { onClick(targetScreen) }, modifier = Modifier.weight(1f)
    ) {
        Text(text = text)
    }
}