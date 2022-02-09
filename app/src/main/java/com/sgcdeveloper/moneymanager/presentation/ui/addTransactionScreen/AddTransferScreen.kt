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
fun AddTransferScreen(addTransactionViewModel: AddTransactionViewModel) {
    LazyColumn(Modifier.padding(12.dp)) {
        item {
            Column(Modifier.fillMaxSize()) {
                DatePicker(addTransactionViewModel)
                AmountPicker(addTransactionViewModel)
                DescriptionItem(addTransactionViewModel)
                WalletPicker(
                    addTransactionViewModel.transactionFromWallet.value, stringResource(
                        id = R.string.from_wallet
                    ),
                    onClick = { addTransactionViewModel.onEvent(AddTransactionEvent.ShowWalletPickerDialog(true)) }
                )
                WalletPicker(
                    addTransactionViewModel.transactionToWallet.value, stringResource(
                        id = R.string.to_wallet
                    ),
                    onClick = { addTransactionViewModel.onEvent(AddTransactionEvent.ShowWalletPickerDialog(false)) }
                )
            }
        }
    }
}