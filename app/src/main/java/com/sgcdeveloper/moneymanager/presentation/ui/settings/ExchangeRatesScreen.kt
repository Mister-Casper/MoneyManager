package com.sgcdeveloper.moneymanager.presentation.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BaseRate
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.addWallet.AddWalletViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.rememberMutableStateListOf
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitViewModel
import com.sgcdeveloper.moneymanager.util.isDouble
import com.sgcdeveloper.moneymanager.util.isWillBeDouble

@Composable
fun ExchangeRatesScreen(navController: NavController, addWalletViewModel: AddWalletViewModel) {
    val rates = rememberMutableStateListOf<BaseRate>()
    if (addWalletViewModel.availableRates.value != null && rates.isEmpty())
        rates.addAll(addWalletViewModel.availableRates.value!!)

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(start = 4.dp, top = 4.dp, end = 4.dp)
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.CenterStart)) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )
                    Text(
                        text = stringResource(id = R.string.exchange_rate),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 22.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 4.dp)
                    )
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {
                        addWalletViewModel.saveRates(rates)
                        navController.popBackStack()
                    },
                    enabled = rates.none { rate -> !(rate.rate.isDouble() && rate.rate.toDouble() > 0) }
                ) {
                    androidx.compose.material.Text(text = stringResource(id = R.string.save), color = white)
                }
            }
        }
        items(rates.size) { item ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
            ) {
                val rate = rates[item]
                TextField(
                    value = rate.currency.name,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                    singleLine = true
                )

                androidx.compose.material.Text(
                    text = stringResource(id = R.string.currency_rate),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp),
                    color = MaterialTheme.colors.secondary
                )

                TextField(
                    value = rate.rate,
                    onValueChange = {
                        if (rate.rate.isWillBeDouble() && rate.rate.length <= InitViewModel.MAX_RATE_LENGTH) {
                            rates.removeAt(item)
                            rates.add(item, BaseRate(rate.currency, it))
                        }
                    },
                    placeholder = {
                        androidx.compose.material.Text(text = rate.rate)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                androidx.compose.material.Text(
                    text = stringResource(id = R.string.rate, addWalletViewModel.defaultCurrency.code, rate.rate, rate.currency.code),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 2.dp),
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }
}