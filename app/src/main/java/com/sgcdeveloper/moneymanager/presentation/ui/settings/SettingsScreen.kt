package com.sgcdeveloper.moneymanager.presentation.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.main.MainViewModel
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.StringSelectorDialog
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*


@Composable
fun SettingsScreen(navController: NavController, darkThemeViewModel: MainViewModel) {
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

    if(darkThemeViewModel.csvPath != Uri.EMPTY){
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        sendIntent.putExtra(Intent.EXTRA_STREAM, darkThemeViewModel.csvPath)
        sendIntent.type = "text/csv"

        LocalContext.current.startActivity(Intent.createChooser(sendIntent, "CSV"))
        darkThemeViewModel.csvPath = Uri.EMPTY
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(32.dp)
                        .padding(start = 12.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
                Text(
                    text = stringResource(id = R.string.settings),
                    fontSize = 24.sp,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 12.dp)
                )
            }
            MenuItem(Modifier.clickable { navController.navigate(Screen.AccountSettings.route) }) {
                Text(
                    text = stringResource(id = R.string.account),
                    Modifier.align(Alignment.CenterStart),
                    fontSize = 20.sp,
                    color = MaterialTheme.colors.onBackground
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            if (darkThemeViewModel.isExistRates()) {
                MenuItem(Modifier.clickable { navController.navigate(Screen.ExchangeRatesScreen.route) }) {
                    Text(
                        text = stringResource(id = R.string.exchange_rate),
                        Modifier.align(Alignment.CenterStart),
                        fontSize = 20.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
            MenuItem(Modifier.clickable { navController.navigate(Screen.TransactionCategoriesSettingsScreen().route) }) {
                Text(
                    text = stringResource(id = R.string.transaction_categories),
                    Modifier.align(Alignment.CenterStart),
                    fontSize = 20.sp,
                    color = MaterialTheme.colors.onBackground
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem {
                Text(
                    text = stringResource(id = R.string.dark_mode),
                    Modifier.align(Alignment.CenterStart),
                    fontSize = 20.sp,
                    color = MaterialTheme.colors.onBackground
                )
                Switch(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    checked = darkThemeViewModel.isDarkTheme.value,
                    onCheckedChange = { darkThemeViewModel.setIsDark(it) }
                )
            }
            MenuItem(Modifier.clickable { darkThemeViewModel.saveCSV()}) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)) {
                    Icon(
                        painter = painterResource(id = R.drawable.csv_icon),
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Text(
                        text = stringResource(id = R.string.export_csv),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                            .padding(start = 4.dp),
                        fontSize = 20.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
            MenuItem(Modifier.clickable { }) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)) {
                    Icon(
                        painter = painterResource(id = R.drawable.excel_icon),
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Text(
                        text = stringResource(id = R.string.export_excel),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                            .padding(start = 4.dp),
                        fontSize = 20.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
            MenuItem(Modifier.clickable { darkThemeViewModel.isShowSelectFirstDayDialog = true }) {
                Column(Modifier.align(Alignment.CenterStart)) {
                    Text(
                        text = stringResource(id = R.string.first_day),
                        fontSize = 20.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                    Text(
                        text = darkThemeViewModel.firstDayOfWeek.value.getDisplayName(
                            TextStyle.FULL,
                            Locale.getDefault()
                        ),
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem(Modifier.clickable { darkThemeViewModel.isShowSelectStartupScreenDialog = true }) {
                Column(Modifier.align(Alignment.CenterStart)) {
                    Text(
                        text = stringResource(id = R.string.startup_screen),
                        fontSize = 20.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                    Text(
                        text = stringResource(id = darkThemeViewModel.defaultStartupScreen.value.resourceId),
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem(Modifier.clickable { darkThemeViewModel.isShowStartupTransactionTypeDialog = true }) {
                Column(Modifier.align(Alignment.CenterStart)) {
                    Text(
                        text = stringResource(id = R.string.startup_transaction_type),
                        fontSize = 20.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                    Text(
                        text = stringResource(id = darkThemeViewModel.defaultStartupTransactionType.value.stringRes),
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem(Modifier.clickable { navController.navigate(Screen.PasswordSettings.route) }) {
                Text(
                    text = stringResource(id = R.string.password_settings),
                    Modifier.align(Alignment.CenterStart),
                    fontSize = 20.sp,
                    color = MaterialTheme.colors.onBackground
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem(Modifier.clickable {
                val appPackageName: String = context.packageName
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: ActivityNotFoundException) {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }) {
                Column(Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.leave_feedback), color = MaterialTheme.colors.onBackground)
                    Text(
                        text = stringResource(id = R.string.leave_feedback_description),
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                }
            }
            MenuItem(Modifier.clickable {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:SGC+Developer")))
                } catch (anfe: ActivityNotFoundException) {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/developer?id=SGC+Developer")
                        )
                    )
                }
            }) {
                Column(Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.other_projects), color = MaterialTheme.colors.onBackground)
                    Text(
                        text = stringResource(id = R.string.other_projects_description),
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.onBackground
                    )
                }
            }

            MenuItem(Modifier.clickable {
                val appPackageName: String = context.packageName
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    context.getString(
                        R.string.check_this_app,
                        "https://play.google.com/store/apps/details?id=$appPackageName"
                    )
                )
                intent.type = "text/plain"
                context.startActivity(Intent.createChooser(intent, "Share To:"))
            }) {
                Text(
                    text = stringResource(id = R.string.share_app),
                    fontSize = 20.sp,
                    modifier = Modifier.align(
                        Alignment.CenterStart
                    ),
                    color = MaterialTheme.colors.onBackground
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
fun MenuItem(modifier: Modifier = Modifier, context: @Composable BoxScope.() -> Unit) {
    Card(
        modifier
            .fillMaxWidth()
            .padding(4.dp)
            .padding(start = 4.dp, end = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(6.dp)
                .padding(start = 6.dp)
        ) {
            context()
        }
    }
}