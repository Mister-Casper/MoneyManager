package com.sgcdeveloper.moneymanager.presentation.ui.registration

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.ui.composables.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpScreen(navController: NavController, registrationViewModel: RegistrationViewModel) {
    val name = rememberSaveable { registrationViewModel.name }
    val login = rememberSaveable { registrationViewModel.login }

    val password = rememberSaveable { registrationViewModel.password }
    val passwordVisibility = remember { registrationViewModel.passwordVisibility }

    val confirmPassword = rememberSaveable { registrationViewModel.confirmPassword }
    val confirmPasswordVisibility = remember { registrationViewModel.confirmPasswordVisibility }

    val isEmailError = remember { registrationViewModel.isEmailError }
    val isLoginError = remember { registrationViewModel.isLoginError }
    val isPasswordError = remember { registrationViewModel.isPasswordError }
    val isPasswordConfirmError = remember { registrationViewModel.isPasswordConfirmError }
    val isShowLoadingDialog = remember { registrationViewModel.showLoadingDialog }

    val isError = remember { registrationViewModel.isSignInError }
    val isConnectInternet = remember { registrationViewModel.isInternetConnection }

    val focusManager = LocalFocusManager.current
    LoadingDialog(isShowLoadingDialog.value)
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(start = 4.dp, top = 4.dp, end = 4.dp)) {
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                Column(Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.lets_get_started),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 20.dp),
                        fontSize = 26.sp
                    )

                    Text(
                        text = stringResource(id = R.string.create_an_account),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 3.dp),
                        fontSize = 14.sp
                    )

                    InputField(
                        name.value,
                        { registrationViewModel.onEvent(RegistrationEvent.ChangeName(it)) },
                        stringResource(id = R.string.username),
                        isLoginError.value,
                        stringResource(id = R.string.login_is_too_shirt),
                        focusManager,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        })
                    )

                    InputField(
                        login.value,
                        { registrationViewModel.onEvent(RegistrationEvent.ChangeLogin(it)) },
                        stringResource(id = R.string.email_address),
                        isEmailError.value,
                        stringResource(id = R.string.email_isnt_valid),
                        focusManager,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        })
                    )

                    PasswordInputField(
                        password.value,
                        { registrationViewModel.onEvent(RegistrationEvent.ChangePassword(it)) },
                        stringResource(id = R.string.password),
                        isPasswordError.value,
                        stringResource(id = R.string.password_short),
                        focusManager,
                        passwordVisibility,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        })
                    )

                    val keyboardController = LocalSoftwareKeyboardController.current

                    PasswordInputField(
                        confirmPassword.value,
                        { registrationViewModel.onEvent(RegistrationEvent.ChangeConfirmPassword(it)) },
                        stringResource(id = R.string.confirm_password),
                        isPasswordConfirmError.value,
                        stringResource(id = R.string.passwords_bot_matches),
                        focusManager,
                        confirmPasswordVisibility,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
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
                                    RegistrationEvent.CreateAccount(
                                        password.value,
                                        login.value,
                                        name.value
                                    )
                                )
                            },
                            modifier = Modifier
                                .padding(top = 16.dp, start = 32.dp, end = 32.dp)
                                .weight(1f),
                            shape = RoundedCornerShape(18.dp),
                        ) {
                            Text(text = stringResource(id = R.string.create_account), color = Color.White)
                        }
                    }

                    SignInGoogle(registrationViewModel)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        Row(Modifier.align(Alignment.Center)) {
                            Text(
                                text = stringResource(id = R.string.have_account),
                            )
                            Text(
                                text = stringResource(id = R.string.login_here),
                                modifier = Modifier.clickable { registrationViewModel.onEvent(RegistrationEvent.MoveToSignIn) },
                                color = blue
                            )
                        }
                    }
                }
            }
        }
        Text(
            text = stringResource(id = R.string.comtimue),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable { navController.navigate(Screen.Init.route) }
                .padding(bottom = 32.dp),
            color = MaterialTheme.colors.primary,
            fontSize = 16.sp
        )
    }
    BackHandler{
        registrationViewModel.onEvent(RegistrationEvent.MoveToSignIn)
    }
}