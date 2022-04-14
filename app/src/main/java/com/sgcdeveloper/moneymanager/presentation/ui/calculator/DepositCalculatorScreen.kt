package com.sgcdeveloper.moneymanager.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.main.MainViewModel
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.gray
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.*
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.StringSelectorDialog
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@Composable
fun DepositCalculatorScreen(navController: NavController, darkThemeViewModel: MainViewModel) {
    val context = LocalContext.current
    var sliderPosition by remember{mutableStateOf(0f)}
    val tipCalculatoorOn = remember{mutableStateOf(false)}
    val mainAccount = rememberSaveable { mutableStateOf("") }
    val deposit = rememberSaveable { mutableStateOf("") }
    val months = rememberSaveable { mutableStateOf("") }
    val percent = rememberSaveable { mutableStateOf("") }
    val textCompate = rememberSaveable { mutableStateOf("") }
    if (darkThemeViewModel.isShowSelectFirstDayDialog) {
        StringSelectorDialog(stringResource(id = R.string.first_day),
            DayOfWeek.values().map { it.getDisplayName(TextStyle.FULL, Locale.getDefault()) },
            darkThemeViewModel.firstDayOfWeek.value.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            ),
            { name ->
                darkThemeViewModel.setFirstDayOfWeek(
                    DayOfWeek.values()
                        .find {
                            name as String == it.getDisplayName(
                                TextStyle.FULL,
                                Locale.getDefault()
                            )
                        }!!
                )
            }, { darkThemeViewModel.isShowSelectFirstDayDialog = false })
    }
    if (darkThemeViewModel.isShowSelectStartupScreenDialog) {
        StringSelectorDialog(stringResource(id = R.string.startup_screen),
            BottomMoneyManagerNavigationScreens.values().map { stringResource(id = it.resourceId) },
            stringResource(darkThemeViewModel.defaultStartupScreen.value.resourceId),
            {
                darkThemeViewModel.setStartupScreen(
                    BottomMoneyManagerNavigationScreens.getByName(
                        it as String,
                        context
                    )
                )
            },
            { darkThemeViewModel.isShowSelectStartupScreenDialog = false })
    }
    if (darkThemeViewModel.isShowStartupTransactionTypeDialog) {
        StringSelectorDialog(stringResource(id = R.string.startup_transaction_type),
            TransactionType.values().map { stringResource(id = it.stringRes) },
            stringResource(darkThemeViewModel.defaultStartupTransactionType.value.stringRes),
            {
                darkThemeViewModel.setStartupTransactionType(
                    TransactionType.getByName(
                        it as String,
                        context
                    )
                )
            },
            { darkThemeViewModel.isShowStartupTransactionTypeDialog = false })
    }
    if (tipCalculatoorOn.value){
        textCompate.value = stringResource(R.string.delete)
    }
    else{
        textCompate.value = stringResource(R.string.compute)
    }
    Column {
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.deposit_calculator),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .padding(20.dp)
                )
            }
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.main_account),
                        color = white,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(20.dp)
                    )
                }
                Row(Modifier.fillMaxWidth()) {
                    TextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        value = mainAccount.value,
                        onValueChange = { newText -> mainAccount.value = newText },
                        modifier = Modifier
                            .padding(20.dp)
                    )
                }
            }
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.deposit),
                        color = white,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(20.dp)
                    )
                }
                Row(Modifier.fillMaxWidth()) {
                    TextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        value = deposit.value,
                        onValueChange = { newText -> deposit.value = newText },
                        modifier = Modifier
                            .padding(20.dp)
                    )
                }
            }
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.months),
                        color = white,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(20.dp)
                    )
                }
                Row(Modifier.fillMaxWidth()) {
                    TextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        value = months.value,
                        onValueChange = { newText -> months.value = newText },
                        modifier = Modifier
                            .padding(20.dp)
                    )
                }
            }
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.percent),
                        color = white,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(20.dp)
                    )
                }
                Row(Modifier.fillMaxWidth()) {
                    TextField(
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        value = percent.value,
                        onValueChange = { newText -> percent.value = newText },
                        modifier = Modifier
                            .padding(20.dp)
                    )
                }
            }
        }
    }

}