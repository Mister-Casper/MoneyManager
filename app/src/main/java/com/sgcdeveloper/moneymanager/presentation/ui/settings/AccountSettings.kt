package com.sgcdeveloper.moneymanager.presentation.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.red

@OptIn(ExperimentalCoilApi::class)
@Composable
fun AccountSettings(navController: NavController, accountSettingsViewModel: AccountSettingsViewModel) {
    val user = FirebaseAuth.getInstance().currentUser
    val url = user?.photoUrl?.toString()

    if (user != null) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 4.dp, top = 4.dp, end = 4.dp)
        ) {
            Row(Modifier.padding(top = 4.dp)) {
                androidx.compose.material.Icon(
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
                    text = stringResource(id = R.string.account_settings),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 4.dp)
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(80.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (url != null) {
                        Icon(
                            painter = rememberImagePainter(
                                data = url,
                                builder = {
                                    transformations(CircleCropTransformation())
                                },
                            ),
                            tint = Color.Unspecified,
                            contentDescription = "",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxSize(),
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.user_icon),
                            tint = MaterialTheme.colors.secondary,
                            contentDescription = "",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxSize(),
                        )
                    }
                }
                Column(
                    Modifier
                        .weight(2f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = user?.email!!,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = accountSettingsViewModel.userName,
                        color = MaterialTheme.colors.secondary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = stringResource(id = R.string.sign_out),
                        color = red,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Screen.SignIn.route)
                                accountSettingsViewModel.signOut()
                            }
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}