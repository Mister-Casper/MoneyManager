package com.sgcdeveloper.moneymanager.presentation.ui.settings

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
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
import androidx.compose.ui.text.input.KeyboardType
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
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.StringSelectorDialog
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*
import kotlin.math.log

@Composable
fun DiscountAndTaxCalculatorScreen(navController: NavController, darkThemeViewModel: MainViewModel) {
    val context = LocalContext.current
    val number = rememberSaveable{mutableStateOf("")}
    val discount = rememberSaveable{mutableStateOf("")}
    val tax = rememberSaveable{mutableStateOf("")}
    val newNumber = rememberSaveable{mutableStateOf("")}
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
        Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.calculator_menu),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)

                )
            }
        Spacer(Modifier.weight(2f))
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = newNumber.value,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)
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
                value = number.value,
                onValueChange = { newText -> number.value = newText },
                modifier = Modifier
                    .padding(20.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.tax),
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
            )
            TextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                value = tax.value,
                onValueChange = { newText -> tax.value = newText },
                modifier = Modifier
                    .padding(20.dp)
            )
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.discount),
                color = white,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
            )
            TextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                value = discount.value,
                onValueChange = { newText -> discount.value = newText },
                modifier = Modifier
                    .padding(20.dp)
            )
        }

        Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.compute),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable {
                            if (newNumber.value != "" || number.value != "" || tax.value != "") {
                                newNumber.value = ((number.value.toDouble() * (100 + tax.value.toDouble()) * 0.01)
                                        * (100 - discount.value.toDouble()) * 0.01).toString()                            }
                        }
                )
            }
        Spacer(Modifier.weight(2f))
    }
}