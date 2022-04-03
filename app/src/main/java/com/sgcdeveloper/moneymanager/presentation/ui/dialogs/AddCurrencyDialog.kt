package com.sgcdeveloper.moneymanager.presentation.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.domain.model.Rate
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitViewModel
import com.sgcdeveloper.moneymanager.util.isDouble
import com.sgcdeveloper.moneymanager.util.isWillBeDouble

@Composable
fun AddCurrencyDialog(defaultCurrency:Currency,currency: Currency, onAdd: (rate: Rate) -> Unit, onCancel: () -> Unit) {
    var rate by remember { mutableStateOf("1.00") }

    AlertDialog(
        containerColor = MaterialTheme.colors.background,
        onDismissRequest = onCancel,
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .clickable { onCancel() }
                    )
                    Text(
                        text = stringResource(id = R.string.add_currency),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp)
                    )
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { onAdd(Rate(currency, rate.toDouble())) },
                    enabled = rate.isDouble() && rate.toDouble() > 0
                ) {
                    Text(text = stringResource(id = R.string.save), color = white)
                }
            }
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                TextField(
                    value = currency.name,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                    singleLine = true
                )

                Text(
                    text = stringResource(id = R.string.currency_rate),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp),
                    color = MaterialTheme.colors.secondary
                )

                TextField(
                    value = rate,
                    onValueChange = {
                        if (rate.isWillBeDouble() && rate.length <= InitViewModel.MAX_RATE_LENGTH) {
                            rate = it
                        }
                    },
                    placeholder = {
                        Text(text = rate)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )
                Text(
                    text = stringResource(id = R.string.rate, defaultCurrency.code, rate, currency.code),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 2.dp),
                    color = MaterialTheme.colors.secondary
                )
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(text = stringResource(id = R.string.cancel), color = white)
            }
        },
        confirmButton = {}
    )
}