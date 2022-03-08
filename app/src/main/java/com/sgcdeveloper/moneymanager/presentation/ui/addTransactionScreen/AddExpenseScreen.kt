package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sgcdeveloper.moneymanager.R

@Composable
fun AddExpenseScreen(addTransactionViewModel: AddTransactionViewModel) {
    LazyColumn(Modifier.padding(start = 4.dp, top = 4.dp, end = 4.dp)) {
        item {
            Column(Modifier.fillMaxSize()) {
                DatePicker(addTransactionViewModel)
                AmountPicker(addTransactionViewModel)
                DescriptionItem(addTransactionViewModel)
                CategoryPicker(addTransactionViewModel, addTransactionViewModel.transactionExpenseCategory.value)
                WalletPicker(addTransactionViewModel.transactionFromWallet.value, stringResource(
                        id = R.string.wallet_selector
                    ),
                    onClick = { addTransactionViewModel.onEvent(AddTransactionEvent.ShowWalletPickerDialog(true)) }
                )
            }
        }
    }
}