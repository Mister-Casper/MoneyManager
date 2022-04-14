package com.sgcdeveloper.moneymanager.presentation.ui.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.ui.composables.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignInScreen(registrationViewModel: RegistrationViewModel,navController:NavController) {
    val login = rememberSaveable { registrationViewModel.login }

    val password = rememberSaveable { registrationViewModel.password }
    val passwordVisibility = remember { registrationViewModel.passwordVisibility }

    val isError = remember { registrationViewModel.isSignInError }
    val isConnectInternet = remember { registrationViewModel.isInternetConnection }
    val isShowLoadingDialog = remember { registrationViewModel.showLoadingDialog }

    val focusManager = LocalFocusManager.current
    LoadingDialog(isShowLoadingDialog.value)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 4.dp, top = 4.dp, end = 4.dp)
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.sign_up_to_save),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    InputField(
                        login.value,
                        { registrationViewModel.onEvent(RegistrationEvent.ChangeLogin(it)) },
                        stringResource(id = R.string.email_address),
                        false,
                        "",
                        focusManager,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        })
                    )

                    val keyboardController = LocalSoftwareKeyboardController.current

                    PasswordInputField(
                        password.value,
                        { registrationViewModel.onEvent(RegistrationEvent.ChangePassword(it)) },
                        stringResource(id = R.string.password),
                        false,
                        "",
                        focusManager,
                        passwordVisibility,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()
                        })
                    )

                    SignInError(isError.value)
                    NoInternetError(isConnectInternet.value)

                    Row(Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                registrationViewModel.onEvent(
                                    RegistrationEvent.SignIn(
                                        password.value,
                                        login.value
                                    )
                                )
                            },
                            modifier = Modifier
                                .padding(top = 16.dp, start = 32.dp, end = 32.dp)
                                .weight(1f),
                            shape = RoundedCornerShape(18.dp),
                        ) {
                            Text(text = stringResource(id = R.string.sign_up), color = Color.White)
                        }
                    }

                    SignInGoogle(registrationViewModel)

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 20.dp)
                    ) {
                        Row(Modifier.align(Alignment.CenterHorizontally)) {
                            Text(
                                text = stringResource(id = R.string.dont_have_account),
                            )
                            Text(
                                text = stringResource(id = R.string.log_in),
                                modifier = Modifier.clickable { registrationViewModel.onEvent(RegistrationEvent.MoveToSignUp) },
                                color = blue
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.forget_password),
                            color = blue,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable {
                                    navController.navigate(Screen.RegainAccess.route)
                                }
                        )
                    }
                }
            }
        }
        Text(
            text = stringResource(id = R.string.comtimue),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable { registrationViewModel.onEvent(RegistrationEvent.Skip) }
                .padding(bottom = 32.dp),
            color = MaterialTheme.colors.primary,
            fontSize = 16.sp
        )
    }
}