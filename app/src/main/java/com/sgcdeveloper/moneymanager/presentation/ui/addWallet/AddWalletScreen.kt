package com.sgcdeveloper.moneymanager.presentation.ui.addWallet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.ColorPicker
import com.sgcdeveloper.moneymanager.presentation.ui.composables.InputField
import com.sgcdeveloper.moneymanager.presentation.ui.composables.WalletCard
import com.sgcdeveloper.moneymanager.presentation.ui.composables.WalletIconPicker
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DeleteWalletDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.SelectCurrenciesDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddWalletScreen(navController: NavController, addWalletViewModel: AddWalletViewModel) {
    val dialog = remember { addWalletViewModel.dialogState }

    if (dialog.value is DialogState.SelectCurrenciesDialogState) {
        SelectCurrenciesDialog(
            currencies = addWalletViewModel.currencies,
            defaultCurrency = addWalletViewModel.walletCurrency.value,
            onAdd = {
                addWalletViewModel.onEvent(WalletEvent.ChangeCurrency(it))
            }) {
            addWalletViewModel.onEvent(WalletEvent.CloseDialog)
        }
    } else if (dialog.value is DialogState.DeleteWalletDialog) {
        DeleteWalletDialog(addWalletViewModel.wallet.value!!, {
            addWalletViewModel.onEvent(WalletEvent.DeleteWallet)
            navController.popBackStack()
        }, {
            addWalletViewModel.onEvent(WalletEvent.CloseDialog)
        })
    }else if (dialog.value is DialogState.InformDialog){
        DeleteWalletDialog((dialog.value as DialogState.InformDialog).information) {
            addWalletViewModel.onEvent(WalletEvent.CloseDialog)
        }
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp, start = 12.dp, end = 12.dp, top = 4.dp)
    ) {
        item {
            Column {
                Row {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable {
                                navController.popBackStack()
                            }
                    )
                    Text(
                        text = stringResource(id = R.string.add_wallet),
                        color = MaterialTheme.colors.secondary,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp)
                            .weight(1f)
                    )
                    if (addWalletViewModel.isEditingMode.value) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete_icon),
                            contentDescription = "",
                            tint = MaterialTheme.colors.secondary,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 4.dp)
                                .size(48.dp)
                                .clickable {
                                    addWalletViewModel.onEvent(WalletEvent.ShowDeleteWalletDialog)
                                }
                        )
                    }
                    Button(onClick = {
                        addWalletViewModel.onEvent(WalletEvent.InsertWallet)
                        navController.popBackStack()
                    }, enabled = addWalletViewModel.walletName.value.isNotEmpty()) {
                        Text(
                            text = stringResource(id = R.string.save),
                            Modifier.align(Alignment.CenterVertically),
                            color = if (addWalletViewModel.walletName.value.isNotEmpty()) white else MaterialTheme.colors.secondary
                        )
                    }
                }
                WalletCard(wallet = addWalletViewModel.wallet.value!!, onClick = {})
                InputField(
                    addWalletViewModel.walletName.value,
                    { addWalletViewModel.onEvent(WalletEvent.ChangeWalletName(it)) },
                    stringResource(id = R.string.wallet_name),
                    false,
                    ""
                )
                val source = remember { MutableInteractionSource() }

                if (source.collectIsPressedAsState().value) {
                    addWalletViewModel.onEvent(WalletEvent.ShowChangeCurrencyDialog)
                }

                TextField(
                    value = addWalletViewModel.walletCurrency.value.name,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, start = 20.dp, end = 20.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                    singleLine = true,
                    trailingIcon = {
                        androidx.compose.material.Icon(imageVector = Icons.Filled.KeyboardArrowDown, "")
                    },
                    interactionSource = source
                )

                Text(
                    text = stringResource(id = R.string.init_amount),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp),
                    color = MaterialTheme.colors.secondary
                )

                TextField(
                    value = addWalletViewModel.walletMoney.value,
                    onValueChange = {
                        addWalletViewModel.onEvent(WalletEvent.ChangeMoney(it))
                    },
                    placeholder = {
                        Text(text = "0")
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, start = 20.dp, end = 20.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )

                Text(
                    text = stringResource(id = R.string.wallet_color),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp),
                    color = MaterialTheme.colors.secondary
                )
                ColorPicker(40.dp, addWalletViewModel.walletColor.value) {
                    addWalletViewModel.onEvent(WalletEvent.ChangeColor(it))
                }

                Text(
                    text = stringResource(id = R.string.wallet_icon),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp),
                    color = MaterialTheme.colors.secondary
                )
                WalletIconPicker(40.dp, addWalletViewModel.walletIcon.value) {
                    addWalletViewModel.onEvent(WalletEvent.ChangeIcon(it))
                }
            }
        }
    }
}