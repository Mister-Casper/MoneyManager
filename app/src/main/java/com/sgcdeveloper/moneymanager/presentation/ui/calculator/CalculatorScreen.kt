package com.sgcdeveloper.moneymanager.presentation.ui.settings

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.ui.vectormath64.length
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.main.MainViewModel
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.StringSelectorDialog
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*
import kotlin.math.log

@Composable
fun CalculatorScreen(navController: NavController, darkThemeViewModel: MainViewModel) {
    val context = LocalContext.current
    val number = rememberSaveable{mutableStateOf("")}
    if (darkThemeViewModel.isShowSelectFirstDayDialog) {
        StringSelectorDialog(stringResource(id = R.string.first_day),
            DayOfWeek.values().map { it.getDisplayName(TextStyle.FULL, Locale.getDefault()) },
            darkThemeViewModel.firstDayOfWeek.value.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            { name ->
                darkThemeViewModel.setFirstDayOfWeek(
                    DayOfWeek.values()
                        .find { name as String == it.getDisplayName(TextStyle.FULL, Locale.getDefault()) }!!
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
            { darkThemeViewModel.setStartupTransactionType(TransactionType.getByName(it as String, context)) },
            { darkThemeViewModel.isShowStartupTransactionTypeDialog = false })
    }

    Column {
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.calculator_menu),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)

                )
            }
        }
        Column(Modifier.weight(5f)) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = number.value,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = "/",
                    color = MaterialTheme.colors.secondary,
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + " / " }
                )
                Text(
                    text = "*",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + " * " }
                )
                Text(
                    text = "=",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable {
                            val arrayNumber = number.value.split(" ")
                            val listNumer = mutableListOf<Double>()
                            val listStr = mutableListOf<String>()
                            val index = 0
                            for (i in arrayNumber) {
                                if (i != "+" && i != "-" && i != "/" && i != "*") {
                                    listNumer.add(i.toDouble())
                                } else {
                                    listStr.add(i)
                                }
                            }
                            Log.d(TAG, listStr.size.toString())
                            Log.d(TAG, listNumer.size.toString())
                            if (listStr.size % 2 != 0 && listNumer.size % 2 == 0) {
                                for (i in listStr) {
                                    if (i == "-") {
                                        listNumer[index + 1] =
                                            listNumer[index] - listNumer[index + 1]
                                    }
                                    if (i == "+") {
                                        listNumer[index + 1] =
                                            listNumer[index] + listNumer[index + 1]
                                    }
                                    if (i == "*") {
                                        listNumer[index + 1] =
                                            listNumer[index] * listNumer[index + 1]
                                    }
                                    if (i == "/") {
                                        listNumer[index + 1] =
                                            listNumer[index] / listNumer[index + 1]
                                    }
                                }
                                number.value = listNumer.last().toString()
                            }
                        }
                )
            }
        }
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = "1",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + "1" }
                )
                Text(
                    text = "2",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + "2" }
                )
                Text(
                    text = "3",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + "3" }
                )
                Text(
                    text = "-",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + " - " }
                )
            }
        }
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = "4",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + "4" }
                )
                Text(
                    text = "5",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + "5" }
                )
                Text(
                    text = "6",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + "6" }
                )
                Text(
                    text = "+",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + " + " }
                )
            }
        }
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = "7",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + "7" }
                )
                Text(
                    text = "8",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + "8" }
                )
                Text(
                    text = "9",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value + "8" }
                )
                Text(
                    text = "del",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.25f)
                        .clickable { number.value = number.value.dropLast(1) }
                )
            }
        }
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = "0",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable { number.value = number.value + "0" }
                )
                Text(
                    text = ".",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable { number.value = number.value + "." }
                )
            }
        }
    }
}