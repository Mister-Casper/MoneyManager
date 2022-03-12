package com.sgcdeveloper.moneymanager.presentation.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DaySelectorDialog
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@Composable
fun SettingsScreen(navController: NavController, darkThemeViewModel: MainViewModel) {
    val context = LocalContext.current

    if (darkThemeViewModel.isShowSelectFirstDayDialog) {
        DaySelectorDialog(stringResource(id = R.string.first_day),
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
        DaySelectorDialog(stringResource(id = R.string.startup_screen),
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
        DaySelectorDialog(stringResource(id = R.string.startup_transaction_type),
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
                    text = stringResource(id = R.string.settings),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 4.dp)
                )
            }
            MenuItem(Modifier.clickable { navController.navigate(Screen.AccountSettings.route) }) {
                Text(
                    text = stringResource(id = R.string.account),
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
            MenuItem {
                Text(
                    text = stringResource(id = R.string.dark_mode),
                    Modifier.align(Alignment.CenterStart),
                    color = white,
                    fontSize = 20.sp
                )
                Switch(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    checked = darkThemeViewModel.isDarkTheme.value,
                    onCheckedChange = { darkThemeViewModel.setIsDark(it) }
                )
            }
            MenuItem(Modifier.clickable { darkThemeViewModel.isShowSelectFirstDayDialog = true }) {
                Column(Modifier.align(Alignment.CenterStart)) {
                    Text(
                        text = stringResource(id = R.string.first_day),
                        color = white,
                        fontSize = 20.sp
                    )
                    Text(
                        text = darkThemeViewModel.firstDayOfWeek.value.getDisplayName(
                            TextStyle.FULL,
                            Locale.getDefault()
                        ),
                        color = white,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp
                    )
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem(Modifier.clickable { darkThemeViewModel.isShowSelectStartupScreenDialog = true }) {
                Column(Modifier.align(Alignment.CenterStart)) {
                    Text(
                        text = stringResource(id = R.string.startup_screen),
                        color = white,
                        fontSize = 20.sp
                    )
                    Text(
                        text = stringResource(id = darkThemeViewModel.defaultStartupScreen.value.resourceId),
                        color = white,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp
                    )
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem(Modifier.clickable { darkThemeViewModel.isShowStartupTransactionTypeDialog = true }) {
                Column(Modifier.align(Alignment.CenterStart)) {
                    Text(
                        text = stringResource(id = R.string.startup_transaction_type),
                        color = white,
                        fontSize = 20.sp
                    )
                    Text(
                        text = stringResource(id = darkThemeViewModel.defaultStartupTransactionType.value.stringRes),
                        color = white,
                        fontWeight = FontWeight.Light,
                        fontSize = 16.sp
                    )
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            MenuItem(Modifier.clickable { navController.navigate(Screen.PasswordSettings.route) }) {
                Text(
                    text = stringResource(id = R.string.password_settings),
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
                    Text(text = stringResource(id = R.string.leave_feedback), color = white)
                    Text(
                        text = stringResource(id = R.string.leave_feedback_description),
                        fontSize = 14.sp,
                        color = white
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
                    Text(text = stringResource(id = R.string.other_projects), color = white)
                    Text(
                        text = stringResource(id = R.string.other_projects_description),
                        fontSize = 14.sp,
                        color = white
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
                    color = white,
                    fontSize = 20.sp,
                    modifier = Modifier.align(
                        Alignment.CenterStart
                    )
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

@Composable
fun MenuItem(modifier: Modifier = Modifier, context: @Composable BoxScope.() -> Unit) {
    Card(
        modifier
            .fillMaxWidth()
            .padding(6.dp)
            .padding(top = 6.dp, bottom = 6.dp)
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