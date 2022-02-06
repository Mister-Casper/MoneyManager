package com.sgcdeveloper.moneymanager.presentation.ui.init

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.SelectCurrenciesDialog

@Composable
fun InitScreen(initViewModel: InitViewModel, navController: NavController) {
    val accountName = remember { initViewModel.userName }
    val money = remember { initViewModel.defaultMoney }
    val currency = remember { initViewModel.currency }
    val defaultWalletName = remember { initViewModel.defaultWalletName }
    val dialogState = remember { initViewModel.dialogState }

    if (dialogState.value is DialogState.SelectCurrenciesDialogState) {
        SelectCurrenciesDialog(initViewModel.currencies, initViewModel.currency.value, {
            initViewModel.onEvent(InitEvent.ChangeCurrency(it))
        }, {
            initViewModel.onEvent(InitEvent.CloseDialog)
        })
    }

    if (initViewModel.isMoveNext.value)
        navController.navigate(Screen.MoneyManagerScreen.route)

    Column(Modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.add_account),
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colors.secondary
        )

        Text(
            text = stringResource(id = R.string.choose_a_name),
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colors.secondary
        )

        TextField(
            value = accountName.value,
            onValueChange = {
                initViewModel.onEvent(InitEvent.ChangeUserName(it))
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Text(
            text = stringResource(id = R.string.choose_a_default_currency),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp),
            color = MaterialTheme.colors.secondary
        )

        val source = remember { MutableInteractionSource() }

        if (source.collectIsPressedAsState().value) {
            initViewModel.onEvent(InitEvent.ShowChangeCurrencyDialog)
        }

        TextField(
            value = currency.value.name,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            singleLine = true,
            trailingIcon = {
                Icon(imageVector = Icons.Filled.KeyboardArrowDown, "")
            },
            interactionSource = source
        )

        Text(
            text = stringResource(id = R.string.choose_a_default_wallet_name),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp),
            color = MaterialTheme.colors.secondary
        )

        TextField(
            value = defaultWalletName.value,
            onValueChange = {
                initViewModel.onEvent(InitEvent.ChangeDefaultWalletName(it))
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Text(
            text = stringResource(id = R.string.money_default),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp),
            color = MaterialTheme.colors.secondary
        )

        TextField(
            value = money.value,
            onValueChange = {
                initViewModel.onEvent(InitEvent.ChangeDefaultMoney(it))
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,imeAction = ImeAction.Next)
        )

        Button(
            onClick = { initViewModel.onEvent(InitEvent.Next) },
            enabled = initViewModel.isNextEnable.value,
            modifier = Modifier
                .padding(top = 16.dp, start = 32.dp, end = 32.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text(text = stringResource(id = R.string.next), color = Color.White)
        }

    }

    BackHandler {
        // Ignore
    }
}