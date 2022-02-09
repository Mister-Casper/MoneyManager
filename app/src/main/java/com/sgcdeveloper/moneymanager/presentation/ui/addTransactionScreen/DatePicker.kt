package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R

@Composable
fun DatePicker(addTransactionViewModel:AddTransactionViewModel) {
    Row(Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(
                id = R.string.date,
                addTransactionViewModel.transactionDate.value.toDateString()
            ), fontSize = 22.sp, modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        Icon(
            painter = painterResource(id = R.drawable.edit_calendar_icon),
            contentDescription = "",
            Modifier
                .size(32.dp)
                .align(Alignment.CenterVertically)
                .clickable { addTransactionViewModel.onEvent(AddTransactionEvent.ShowChangeDateDialog) }
        )
    }
}