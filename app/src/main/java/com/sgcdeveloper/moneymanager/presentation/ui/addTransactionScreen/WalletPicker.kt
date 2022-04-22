package com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.presentation.theme.blue

@Composable
fun WalletPicker(
    isFrom:Boolean,
    addTransactionViewModel: AddTransactionViewModel,
    defaultWallet: Wallet? = null,
    textDescription: String,
    onClick: () -> Unit
) {
    val source = remember { MutableInteractionSource() }

    if (source.collectIsPressedAsState().value) {
        onClick()
    }

    Row(Modifier.fillMaxWidth().padding(start = 10.dp,end = 10.dp)) {
        TextField(
            value = defaultWallet?.name ?: "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 12.dp)
                .weight(1f),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            singleLine = true,
            trailingIcon = {
                Icon(imageVector = Icons.Filled.KeyboardArrowDown, "")
            }, placeholder = {
                Text(text = textDescription)
            }, interactionSource = source
        )
        if (isFrom && addTransactionViewModel.isShowFromWalletRate){
            Column(
                Modifier
                    .align(Alignment.Bottom)
                    .padding(start = 4.dp)
                    .clickable { addTransactionViewModel.changeRate(false) }) {
                Icon(
                    painter = painterResource(id = R.drawable.repeat_icon),
                    "",
                    Modifier
                        .size(32.dp)
                        .align(Alignment.CenterHorizontally),
                    tint = MaterialTheme.colors.secondary
                )
                androidx.compose.material3.Text(
                    text = addTransactionViewModel.fromWalletRate,
                    color = if (addTransactionViewModel.recurringInterval.value != RecurringInterval.None) blue else MaterialTheme.colors.secondary,
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    ),
                    fontSize = 12.sp
                )
            }
        }else if (! isFrom && addTransactionViewModel.isShowToWalletRate){
            Column(
                Modifier
                    .align(Alignment.Bottom)
                    .padding(start = 4.dp)
                    .clickable { addTransactionViewModel.changeRate(true) }) {
                Icon(
                    painter = painterResource(id = R.drawable.repeat_icon),
                    "",
                    Modifier
                        .size(32.dp)
                        .align(Alignment.CenterHorizontally),
                    tint = MaterialTheme.colors.secondary
                )
                androidx.compose.material3.Text(
                    text = addTransactionViewModel.toWalletRate,
                    color = if (addTransactionViewModel.recurringInterval.value != RecurringInterval.None) blue else MaterialTheme.colors.secondary,
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    ),
                    fontSize = 12.sp
                )
            }
        }
    }
}