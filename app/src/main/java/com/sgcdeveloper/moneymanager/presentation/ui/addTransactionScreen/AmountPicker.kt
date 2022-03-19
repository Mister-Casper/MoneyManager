package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sgcdeveloper.moneymanager.R

@Composable
fun ColumnScope.AmountPicker(addTransactionViewModel: AddTransactionViewModel) {
    TextField(
        value = addTransactionViewModel.transactionAmount.value,
        onValueChange = { addTransactionViewModel.onEvent(AddTransactionEvent.ChangeTransactionAmount(it)) },
        placeholder = { Text(text = "0") },
        label = { Text(stringResource(id = R.string.amount, addTransactionViewModel.formattedTransactionAmount.value)) },
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, start = 20.dp, end = 20.dp)
            .align(Alignment.CenterHorizontally),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary)
    )
}