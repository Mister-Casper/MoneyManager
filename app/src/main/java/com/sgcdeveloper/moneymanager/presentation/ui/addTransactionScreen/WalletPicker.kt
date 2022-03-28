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
import androidx.compose.ui.unit.dp
import com.sgcdeveloper.moneymanager.domain.model.Wallet

@Composable
fun WalletPicker(defaultWallet: Wallet? = null, textDescription: String, onClick: () -> Unit) {
    val source = remember { MutableInteractionSource() }

    if (source.collectIsPressedAsState().value) {
        onClick()
    }

    Row(Modifier.fillMaxWidth()) {
        TextField(
            value = defaultWallet?.name ?: "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 12.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            singleLine = true,
            trailingIcon = {
                Icon(imageVector = Icons.Filled.KeyboardArrowDown, "")
            }, placeholder = {
                Text(text = textDescription)
            }, interactionSource = source
        )
    }
}