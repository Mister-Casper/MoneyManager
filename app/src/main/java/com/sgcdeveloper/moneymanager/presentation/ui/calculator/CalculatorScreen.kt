package com.sgcdeveloper.moneymanager.presentation.ui.calculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.main.MainViewModel
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.StringSelectorDialog
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalculatorScreen(navController: NavController, darkThemeViewModel: MainViewModel) {
    val context = LocalContext.current

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
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.calculator_menu),
                color = MaterialTheme.colors.secondary,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f)
            )
        }
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = "1",
                color = MaterialTheme.colors.secondary,
                fontSize = 22.sp,
                modifier = Modifier.weight(0.3f)
            )
            Text(
                text = "2",
                color = MaterialTheme.colors.secondary,
                fontSize = 22.sp,
                modifier = Modifier.weight(0.3f)
            )
            Text(
                text = "3",
                color = MaterialTheme.colors.secondary,
                fontSize = 22.sp,
                modifier = Modifier.weight(0.3f)
            )
        }
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = "4",
                color = MaterialTheme.colors.secondary,
                fontSize = 22.sp,
                modifier = Modifier.weight(0.3f)
            )
            Text(
                text = "5",
                color = MaterialTheme.colors.secondary,
                fontSize = 22.sp,
                modifier = Modifier.weight(0.3f)
            )
            Text(
                text = "6",
                color = MaterialTheme.colors.secondary,
                fontSize = 22.sp,
                modifier = Modifier.weight(0.3f)
            )
        }
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = "7",
                color = MaterialTheme.colors.secondary,
                fontSize = 22.sp,
                modifier = Modifier.weight(0.3f)
            )
            Text(
                text = "8",
                color = MaterialTheme.colors.secondary,
                fontSize = 22.sp,
                modifier = Modifier.weight(0.3f)
            )
            Text(
                text = "9",
                color = MaterialTheme.colors.secondary,
                fontSize = 22.sp,
                modifier = Modifier.weight(0.3f)
            )
        }
    }
}