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
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@Composable
fun TipCalculatorScreen(navController: NavController, darkThemeViewModel: MainViewModel) {
    val context = LocalContext.current
    val split = rememberSaveable { mutableStateOf("1") }
    var sliderPosition by remember{mutableStateOf(0f)}
    val tipCalculatoorOn = remember{mutableStateOf(false)}
    val tipAmount = rememberSaveable { mutableStateOf("") }
    val everyRayment = rememberSaveable { mutableStateOf("") }
    val checkTips = rememberSaveable { mutableStateOf("") }
    val totalAmountReceipt = rememberSaveable { mutableStateOf("") }
    val textCompate = rememberSaveable { mutableStateOf("") }
    val money = rememberSaveable{mutableStateOf("") }
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
        Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.tip_calculator),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .padding(20.dp)
                )
            }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.check),
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
            )
            TextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                value = money.value,
                onValueChange = { newText -> money.value = newText },
                modifier = Modifier
                    .padding(20.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.split),
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
            )
            TextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                value = split.value,
                onValueChange = { newText -> split.value = newText },
                modifier = Modifier
                    .padding(20.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.tips),
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(0.25f)
                    .padding(20.dp)
            )
            Text(
                text = "$sliderPosition",
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(0.25f)
                    .padding(20.dp)
            )
            Text(
                text = stringResource(id = R.string.percent),
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(0.25f)
                    .padding(20.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            Slider(
                modifier = Modifier
                    .padding(20.dp),
                value = sliderPosition,
                valueRange = 0f..100f,
                steps = 99,
                onValueChange = { sliderPosition = it },
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFB71C1C),
                    activeTrackColor = Color(0xFFEF9A9A),
                    inactiveTrackColor = Color(0xFFFFEBEE),
                    inactiveTickColor = Color(0xFFEF9A9A),
                    activeTickColor = Color(0xFFB71C1C)
                )
            )
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = textCompate.value,
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .clickable {
                        if (tipCalculatoorOn.value) {
                            tipCalculatoorOn.value = false
                            tipAmount.value = ""
                            totalAmountReceipt.value = ""
                            checkTips.value = ""
                            everyRayment.value = ""
                        }
                        else if (money.value != "" || split.value != "") {
                            tipCalculatoorOn.value = true
                            tipAmount.value =
                                ((money.value.toDouble() * sliderPosition / 100).toBigDecimal().setScale(2, RoundingMode.UP)).toString()
                            totalAmountReceipt.value =
                                ((money.value.toDouble() + tipAmount.value.toDouble()).toBigDecimal().setScale(2, RoundingMode.UP)).toString()
                            checkTips.value =
                                ((tipAmount.value.toDouble() / split.value.toDouble()).toBigDecimal().setScale(2, RoundingMode.UP)).toString()
                            everyRayment.value =
                                ((totalAmountReceipt.value.toDouble() / split.value.toDouble()).toBigDecimal().setScale(2, RoundingMode.UP)).toString()
                        }
                    }
            )
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.tip_amount),
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .weight(0.5f)
            )
            Text(
                text = tipAmount.value,
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .weight(0.5f)
            )
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.Total_amount_receipt),
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .weight(0.5f)
            )
            Text(
                text = totalAmountReceipt.value,
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .weight(0.5f)
            )
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.Check_tips),
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .weight(0.5f)
            )
            Text(
                text = checkTips.value,
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .weight(0.5f)
            )
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.Every_payment),
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .weight(0.5f)
            )
            Text(
                text = everyRayment.value,
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
                    .weight(0.5f)
            )
            }
        }

}