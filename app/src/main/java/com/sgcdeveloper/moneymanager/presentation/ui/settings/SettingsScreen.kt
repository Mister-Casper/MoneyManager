package com.sgcdeveloper.moneymanager.presentation.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.main.DarkThemeViewModel
import com.sgcdeveloper.moneymanager.presentation.theme.white

@Composable
fun SettingsScreen(navController: NavController, darkThemeViewModel: DarkThemeViewModel) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 60.dp)
            .padding(12.dp)
    ) {
        Row(Modifier.padding(top = 4.dp)) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "",
                tint = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
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
        MenuItem {
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
    }
}

@Composable
fun MenuItem(context: @Composable BoxScope.() -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .padding(top = 6.dp, bottom = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(6.dp)
        ) {
            context()
        }
    }
}