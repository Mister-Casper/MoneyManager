package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sgcdeveloper.moneymanager.R

@Composable
fun DatePicker(addTransactionViewModel: AddTransactionViewModel) {
    val source = remember { MutableInteractionSource() }

    if (source.collectIsPressedAsState().value) {
        addTransactionViewModel.onEvent(AddTransactionEvent.ShowChangeDateDialog)
    }

    Row(Modifier.fillMaxWidth()) {
        TextField(
            value = stringResource(
                id = R.string.date,
                addTransactionViewModel.transactionDate.value.toDateString()
            ),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            singleLine = true,
            trailingIcon = {
                Icon(painter = painterResource(id = R.drawable.edit_calendar_icon), "",Modifier.size(32.dp))
            }, interactionSource = source
        )
    }
}