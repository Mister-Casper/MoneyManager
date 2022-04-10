package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.ui.composables.InputField

@Composable
fun ColumnScope.DescriptionItem(addTransactionViewModel: AddTransactionViewModel) {
    val focusManager = LocalFocusManager.current

    InputField(
        addTransactionViewModel.transactionDescription.value,
        { addTransactionViewModel.onEvent(AddTransactionEvent.ChangeTransactionDescription(it)) },
        stringResource(id = R.string.description),
        false,
        "",
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        })
    )
}