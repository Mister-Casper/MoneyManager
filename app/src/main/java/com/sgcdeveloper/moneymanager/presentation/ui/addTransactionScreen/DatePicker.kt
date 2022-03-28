package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval
import com.sgcdeveloper.moneymanager.presentation.theme.blue

@Composable
fun DatePicker(addTransactionViewModel: AddTransactionViewModel) {
    val source = remember { MutableInteractionSource() }

    if (source.collectIsPressedAsState().value) {
        addTransactionViewModel.onEvent(AddTransactionEvent.ShowChangeDateDialog)
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
    ) {
        TextField(
            value = stringResource(
                id = R.string.date,
                addTransactionViewModel.transactionDate.value.toDateString()
            ),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 12.dp)
                .weight(1f),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            singleLine = true,
            trailingIcon = {
                Icon(painter = painterResource(id = R.drawable.edit_calendar_icon), "", Modifier.size(32.dp))
            }, interactionSource = source
        )
        Column(
            Modifier
                .align(Alignment.Bottom)
                .padding(start = 4.dp)
                .clickable { addTransactionViewModel.onEvent(AddTransactionEvent.ShowRepeatIntervalDialog) }) {
            Icon(
                painter = painterResource(id = R.drawable.repeat_icon),
                "",
                Modifier
                    .size(32.dp)
                    .align(Alignment.CenterHorizontally),
                tint = MaterialTheme.colors.secondary
            )
            Text(
                text = stringResource(id = if (addTransactionViewModel.recurringInterval.value == RecurringInterval.None) R.string.recurring else addTransactionViewModel.recurringInterval.value.recurring.titleRes),
                color = if (addTransactionViewModel.recurringInterval.value != RecurringInterval.None) blue else MaterialTheme.colors.secondary,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                ),
                fontSize = 12.sp
            )
        }
    }
}