package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.ui.composables.InputField

@Composable
fun ColumnScope.DescriptionItem(addTransactionViewModel:AddTransactionViewModel) {
    InputField(
        addTransactionViewModel.transactionDescription.value,
        { addTransactionViewModel.onEvent(AddTransactionEvent.ChangeTransactionDescription(it)) },
        stringResource(id = R.string.description),
        false,
        "",
    )
}