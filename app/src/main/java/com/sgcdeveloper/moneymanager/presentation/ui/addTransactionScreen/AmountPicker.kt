package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R

@Composable
fun AmountPicker(addTransactionViewModel:AddTransactionViewModel) {
    var symbol = "$"
    if(addTransactionViewModel.transactionFromWallet.value != null)
        symbol = addTransactionViewModel.transactionFromWallet.value!!.currency.symbol

    Row(Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Text(
            text = stringResource(id = R.string.amount,  symbol),
            fontSize = 22.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        TextField(
            value = addTransactionViewModel.transactionAmount.value,
            onValueChange = { addTransactionViewModel.onEvent(AddTransactionEvent.ChangeTransactionAmount(it)) },
            placeholder = { Text(text = "0") },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}