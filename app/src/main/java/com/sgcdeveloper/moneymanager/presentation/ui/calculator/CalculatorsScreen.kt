package com.sgcdeveloper.moneymanager.presentation.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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

@Composable
fun CalculatorsScreen(navController: NavController, darkThemeViewModel: MainViewModel) {
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

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(start = 4.dp, top = 4.dp, end = 4.dp)
    ) {
        item {
            Row(Modifier.padding(top = 4.dp)) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(40.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
                Text(
                    text = stringResource(id = R.string.calculator_menu),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 4.dp)
                )
            }
            MenuItem(Modifier.clickable { navController.navigate(Screen.Calculator.route) }) {
                Text(
                    text = stringResource(id = R.string.calculator_menu),
                    Modifier.align(Alignment.CenterStart),
                    color = white,
                    fontSize = 20.sp
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem(Modifier.clickable { navController.navigate(Screen.AccountSettings.route) }) {
                Text(
                    text = stringResource(id = R.string.tip_calculator),
                    Modifier.align(Alignment.CenterStart),
                    color = white,
                    fontSize = 20.sp
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem(Modifier.clickable { navController.navigate(Screen.AccountSettings.route) }) {
                Text(
                    text = stringResource(id = R.string.interest_calculator),
                    Modifier.align(Alignment.CenterStart),
                    color = white,
                    fontSize = 20.sp
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem(Modifier.clickable { navController.navigate(Screen.AccountSettings.route) }) {
                Text(
                    text = stringResource(id = R.string.credit_card_payoff_calculator),
                    Modifier.align(Alignment.CenterStart),
                    color = white,
                    fontSize = 20.sp
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem(Modifier.clickable { navController.navigate(Screen.AccountSettings.route) }) {
                Text(
                    text = stringResource(id = R.string.loan_calculator),
                    Modifier.align(Alignment.CenterStart),
                    color = white,
                    fontSize = 20.sp
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem(Modifier.clickable { navController.navigate(Screen.AccountSettings.route) }) {
                Text(
                    text = stringResource(id = R.string.discount_and_tax_calculator),
                    Modifier.align(Alignment.CenterStart),
                    color = white,
                    fontSize = 20.sp
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}