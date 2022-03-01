package com.sgcdeveloper.moneymanager.presentation.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.white

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PasswordSettings(navController: NavController, passwordSettingsViewModel: PasswordSettingsViewModel) {
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
                        .clickable {
                            navController.popBackStack()
                        }
                )
                Text(
                    text = stringResource(id = R.string.password_settings),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 4.dp)
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                val context = LocalContext.current
                Button(
                    onClick = { passwordSettingsViewModel.createPassword(context) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    if (passwordSettingsViewModel.isCanChange.value)
                        Text(text = stringResource(id = R.string.delete_passwrod), color = white, fontSize = 18.sp)
                    else
                        Text(text = stringResource(id = R.string.choose_password), color = white, fontSize = 18.sp)
                }
                Button(
                    onClick = { passwordSettingsViewModel.changePassword(context) },
                    enabled = passwordSettingsViewModel.isCanChange.value,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 6.dp)
                ) {
                    Text(text = stringResource(id = R.string.change_password), color = white, fontSize = 18.sp)
                }
            }
        }
    }
}