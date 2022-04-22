package com.sgcdeveloper.moneymanager.presentation.ui.addWallet

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.ColorPicker
import com.sgcdeveloper.moneymanager.presentation.ui.composables.IconPicker
import com.sgcdeveloper.moneymanager.presentation.ui.composables.InputField
import com.sgcdeveloper.moneymanager.presentation.ui.composables.WalletCard
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.*
import com.sgcdeveloper.moneymanager.util.wallet_icons

@OptIn(ExperimentalFoundationApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun AddWalletScreen(navController: NavController, addWalletViewModel: AddWalletViewModel) {
    val dialog = remember { addWalletViewModel.dialogState }
    val dialogBackOpen = remember { addWalletViewModel.backDialog }
    val signalBack = remember { addWalletViewModel.back }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    if (dialog.value is DialogState.SelectCurrenciesDialogState) {
        SelectCurrenciesDialog(
            currencies = addWalletViewModel.currencies,
            defaultCurrency = addWalletViewModel.walletCurrency.value!!,
            onAdd = {
                addWalletViewModel.onEvent(WalletEvent.ChangeCurrency(it))
                focusManager.moveFocus(FocusDirection.Down)
            }) {
            addWalletViewModel.onEvent(WalletEvent.CloseDialog)
        }
    } else if (dialog.value is DialogState.DeleteWalletDialog) {
        DeleteWalletDialog(addWalletViewModel.wallet.value!!, {
            addWalletViewModel.onEvent(WalletEvent.DeleteWallet)
            moveBack(navController)
        }, {
            addWalletViewModel.onEvent(WalletEvent.CloseDialog)
        })
    } else if (dialog.value is DialogState.InformDialog) {
        InformationDialog((dialog.value as DialogState.InformDialog).information) {
            addWalletViewModel.onEvent(WalletEvent.CloseDialog)
        }
    } else if (dialog.value is DialogState.AddCurrencyRateDialog) {
        AddCurrencyDialog(
            defaultCurrency = addWalletViewModel.defaultCurrency,
            currency = (dialog.value as DialogState.AddCurrencyRateDialog).currency,
            onAdd = {
                addWalletViewModel.onEvent(WalletEvent.AddCurrency(it))
            }) {
            addWalletViewModel.onEvent(WalletEvent.ShowChangeCurrencyDialog)
        }
    }

    if (dialogBackOpen.value) {
        DialogBack(dialogBackOpen.value, signalBack.value,
            signalReturn = {
                signalBack.value = false
                dialogBackOpen.value = false
            },
            dialogOpen = {
                dialogBackOpen.value = false
                navController.popBackStack()
            }
        )
    }
    if (signalBack.value) {
        signalBack.value = false
        navController.popBackStack()
    }
    LazyColumn(
        Modifier
            .fillMaxSize()
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.surface)
                        .padding(top = 8.dp, bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(32.dp)
                            .clickable {
                                dialogBackOpen.value = true

                            },
                        tint = MaterialTheme.colors.onBackground
                    )
                    Text(
                        text = stringResource(id = R.string.add_wallet),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 2.dp)
                            .weight(1f)
                    )
                    if (addWalletViewModel.isEditingMode.value) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete_icon),
                            contentDescription = "",
                            tint = MaterialTheme.colors.onBackground,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 2.dp)
                                .size(40.dp)
                                .clickable {
                                    addWalletViewModel.onEvent(WalletEvent.ShowDeleteWalletDialog)
                                }
                        )
                    }
                    Button(
                        onClick = {
                            addWalletViewModel.onEvent(WalletEvent.InsertWallet)
                            if (addWalletViewModel.isAutoReturn) {
                                if (navController.backQueue
                                        .dropLast(1)
                                        .last().destination.route!! == "WalletScreen/{wallet}"
                                )
                                    navController.popBackStack(BottomMoneyManagerNavigationScreens.Home.route, false)
                                else
                                    navController.popBackStack()
                            } else
                                Toast.makeText(context, context.getString(R.string.wallet_added), Toast.LENGTH_LONG).show()
                        },
                        enabled = addWalletViewModel.walletName.value.isNotEmpty(),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.save),
                            Modifier.align(Alignment.CenterVertically),
                            color = if (addWalletViewModel.walletName.value.isNotEmpty()) white else MaterialTheme.colors.onBackground
                        )
                    }
                }
                WalletCard(wallet = addWalletViewModel.wallet.value!!, onClick = {})

                InputField(
                    addWalletViewModel.walletName.value,
                    { addWalletViewModel.onEvent(WalletEvent.ChangeWalletName(it)) },
                    stringResource(id = R.string.wallet_name),
                    false,
                    "",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions (onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    })
                )
                val source = remember { MutableInteractionSource() }

                if (source.collectIsPressedAsState().value) {
                    addWalletViewModel.onEvent(WalletEvent.ShowChangeCurrencyDialog)
                }

                TextField(
                    value = addWalletViewModel.walletCurrency.value!!.name,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        androidx.compose.material.Icon(imageVector = Icons.Filled.KeyboardArrowDown, "")
                    },
                    interactionSource = source,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Text(
                    text = stringResource(id = R.string.init_amount),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp),
                    color = MaterialTheme.colors.onBackground
                )

                val keyboardController = LocalSoftwareKeyboardController.current

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
                        .padding(top = 8.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() })
                )

                Text(
                    text = stringResource(id = R.string.wallet_color),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp),
                    color = MaterialTheme.colors.onBackground
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
                    color = MaterialTheme.colors.onBackground
                )
                IconPicker(wallet_icons,40.dp, addWalletViewModel.walletIcon.value) {
                    addWalletViewModel.onEvent(WalletEvent.ChangeIcon(it))
                }
            }
        }
    }
    BackHandler {
        dialogBackOpen.value = true
    }
}

fun moveBack(navController: NavController) {
    if (navController.backQueue
            .dropLast(1)
            .last().destination.route!! == "WalletScreen/{wallet}"
    )
        navController.popBackStack(BottomMoneyManagerNavigationScreens.Home.route, false)
    else
        navController.popBackStack()
}