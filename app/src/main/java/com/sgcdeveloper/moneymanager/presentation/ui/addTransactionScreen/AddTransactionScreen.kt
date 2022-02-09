package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DatePicker
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.SelectTransactionCategoryDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.WalletPickerDialog

@Composable
fun AddTransactionScreen(addTransactionViewModel: AddTransactionViewModel, navController: NavController) {
    val currentTransactionScreen = remember { addTransactionViewModel.currentScreen }
    val currentScreenName = remember { addTransactionViewModel.currentScreenName }
    val dialog = remember { addTransactionViewModel.dialogState }
    val wallets = addTransactionViewModel.wallets.observeAsState()

    if (dialog.value is DialogState.DatePickerDialog) {
        DatePicker(
            defaultDate = addTransactionViewModel.transactionDate.value,
            onDateSelected = { addTransactionViewModel.onEvent(AddTransactionEvent.ChangeTransactionDate(it)) },
            onDismissRequest = {
                addTransactionViewModel.onEvent(AddTransactionEvent.CloseDialog)
            })
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
            }) {
            addTransactionViewModel.onEvent(AddTransactionEvent.CloseDialog)
        }
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
                    .clickable { navController.popBackStack() }
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
            Button(onClick = {
                addTransactionViewModel.onEvent(AddTransactionEvent.InsertTransaction)
                navController.popBackStack()
            }, enabled = addTransactionViewModel.isTransactionCanBeSaved.value) {
                Text(
                    text = stringResource(id = R.string.save),
                    Modifier.align(Alignment.CenterVertically),
                    color = if (addTransactionViewModel.isTransactionCanBeSaved.value) white else MaterialTheme.colors.secondary
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            transactionNavigationButton(
                addTransactionViewModel,
                currentTransactionScreen.value,
                TransactionScreen.Income,
                stringResource(id = R.string.income)
            )
            transactionNavigationButton(
                addTransactionViewModel,
                currentTransactionScreen.value,
                TransactionScreen.Expense,
                stringResource(id = R.string.expense)
            )
            transactionNavigationButton(
                addTransactionViewModel,
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
}


@Composable
fun RowScope.transactionNavigationButton(
    addTransactionViewModel: AddTransactionViewModel,
    currentTransactionScreen: TransactionScreen,
    targetScreen: TransactionScreen,
    text: String
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (currentTransactionScreen == targetScreen) blue else MaterialTheme.colors.background,
            contentColor = if (currentTransactionScreen == targetScreen) white else MaterialTheme.colors.secondary
        ),
        onClick = {
            addTransactionViewModel.onEvent(AddTransactionEvent.ChangeAddTransactionScreen(targetScreen))
        }, modifier = Modifier.weight(1f)
    ) {
        Text(text = text)
    }
}