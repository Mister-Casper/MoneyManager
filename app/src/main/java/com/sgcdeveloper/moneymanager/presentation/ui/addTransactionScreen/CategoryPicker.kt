package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory

@Composable
fun CategoryPicker(addTransactionViewModel: AddTransactionViewModel,transactionCategory:TransactionCategory) {
    val source = remember { MutableInteractionSource() }

    if (source.collectIsPressedAsState().value) {
        addTransactionViewModel.onEvent(AddTransactionEvent.ShowTransactionCategoryPickerDialog)
    }

    Row(Modifier.fillMaxWidth()) {
        TextField(
            value = stringResource(id = transactionCategory.description),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            singleLine = true,
            trailingIcon = {
                Icon(imageVector = Icons.Filled.KeyboardArrowDown, "")
            }, placeholder = {
                Text(text = stringResource(id = R.string.select_category))
            }, interactionSource = source
        )
    }
}