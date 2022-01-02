package com.sgcdeveloper.moneymanager.presentation.ui.registration

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.sgcdeveloper.moneymanager.R


private lateinit var authResultLauncher: ActivityResultLauncher<Intent>
private lateinit var googleSignInClient: GoogleSignInClient

@Composable
fun StartScreen(
    registrationViewModel: RegistrationViewModel,
    init: ActivityResultLauncher<Intent>,
    isGoogleSigned: MutableState<Boolean>
) {
    val isSigningIn = rememberSaveable { mutableStateOf(true) }
    registrationViewModel.isSignInError.value = false
    authResultLauncher = init

    if (isSigningIn.value) {
        signInScreen(
            { isSigningIn.value = false },
            { registrationViewModel.onEvent(RegistrationEvent.Skip) },
            registrationViewModel
        )
    } else {
        signUpScreen(
            { isSigningIn.value = true },
            { registrationViewModel.onEvent(RegistrationEvent.Skip) },
            registrationViewModel
        )
    }

    if (isGoogleSigned.value)
        registrationViewModel.onEvent(RegistrationEvent.Skip)
}

private fun init(context: Context) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    googleSignInClient = GoogleSignIn.getClient(context, gso)
}

@Composable
fun signInScreen(onSignUp: () -> Unit, onSkip: () -> Unit, registrationViewModel: RegistrationViewModel) {
    val login = rememberSaveable { mutableStateOf("") }

    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }

    val isError = remember { registrationViewModel.isSignInError }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Text(
                text = stringResource(id = R.string.sign_up_to_save),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp),
                fontSize = 18.sp,
                color = MaterialTheme.colors.secondary
            )

            val focusManager = LocalFocusManager.current

            TextField(
                value = login.value,
                onValueChange = {
                    login.value = it
                    isError.value = false
                },
                label = { Text(stringResource(id = R.string.email_address)) },
                placeholder = { Text(stringResource(id = R.string.email_address)) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            TextField(
                singleLine = true,
                value = password.value,
                onValueChange = {
                    password.value = it
                    isError.value = false
                },
                label = {
                    Text(
                        stringResource(
                            id = R.string.password
                        )
                    )
                },
                placeholder = {
                    Text(
                        stringResource(
                            id = R.string.password
                        )
                    )
                },
                visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisibility.value)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = {
                        passwordVisibility.value = !passwordVisibility.value
                    }) {
                        Icon(imageVector = image, "")
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary)
            )

            if (isError.value) {
                Text(
                    text = stringResource(id = R.string.cant_login),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 20.dp, top = 12.dp),
                )
            }

            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = { registrationViewModel.onEvent(RegistrationEvent.SignIn(password.value, login.value)) },
                    modifier = Modifier
                        .padding(top = 16.dp, start = 32.dp, end = 32.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text(text = stringResource(id = R.string.sign_up), color = Color.White)
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Divider(
                    color = MaterialTheme.colors.secondary,
                    thickness = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = stringResource(id = R.string.or),
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Divider(
                    color = MaterialTheme.colors.secondary,
                    thickness = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
            }

            val isGoogleIn = remember { mutableStateOf(false) }

            if (isGoogleIn.value) {
                init(LocalContext.current)
                val signInIntent = googleSignInClient.signInIntent
                authResultLauncher.launch(signInIntent)
                isGoogleIn.value = false
            }

            SignInGoogleButton {
                isGoogleIn.value = true
            }

            Divider(
                color = MaterialTheme.colors.secondary,
                thickness = 1.dp,
                modifier = Modifier.padding(top = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Row(Modifier.align(Alignment.Center)) {
                    Text(text = stringResource(id = R.string.dont_have_account), color = MaterialTheme.colors.secondary)
                    Text(
                        text = stringResource(id = R.string.log_in),
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.clickable { onSignUp() }
                    )
                }
            }
        }
        Text(
            text = stringResource(id = R.string.comtimue),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable { onSkip() }
                .padding(bottom = 32.dp),
            color = MaterialTheme.colors.primary,
            fontSize = 16.sp
        )
    }
}

@Composable
fun signUpScreen(onSignUp: () -> Unit, onSkip: () -> Unit, registrationViewModel: RegistrationViewModel) {
    val name = rememberSaveable { mutableStateOf("") }
    val login = rememberSaveable { mutableStateOf("") }

    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }

    val confirmPassword = rememberSaveable { mutableStateOf("") }
    val confirmPasswordVisibility = remember { mutableStateOf(false) }

    val isEmailError = remember { mutableStateOf(false) }
    val isLoginError = remember { mutableStateOf(false) }
    val isPasswordError = remember { mutableStateOf(false) }
    val isPasswordConfirmError = remember { mutableStateOf(false) }

    val isError = remember { registrationViewModel.isSignInError }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Text(
                text = stringResource(id = R.string.lets_get_started),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp),
                fontSize = 26.sp,
                color = MaterialTheme.colors.secondary
            )

            Text(
                text = stringResource(id = R.string.create_an_account),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 3.dp),
                fontSize = 14.sp,
                color = MaterialTheme.colors.secondary
            )

            val focusManager = LocalFocusManager.current

            TextField(
                singleLine = true,
                value = name.value,
                onValueChange = {
                    name.value = it
                    isLoginError.value = false
                },
                label = { Text(stringResource(id = R.string.username)) },
                placeholder = { Text(stringResource(id = R.string.username)) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            if (isLoginError.value) {
                Text(
                    text = stringResource(id = R.string.short_login),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }

            TextField(
                singleLine = true,
                value = login.value,
                onValueChange = {
                    login.value = it
                    isEmailError.value = false
                },
                label = { Text(stringResource(id = R.string.email_address)) },
                placeholder = { Text(stringResource(id = R.string.email_address)) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                isError = isEmailError.value,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            if (isEmailError.value) {
                Text(
                    text = stringResource(id = R.string.email_isnt_valid),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }

            TextField(
                keyboardActions = KeyboardActions(
                    onNext = {focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                value = password.value,
                onValueChange = {
                    password.value = it
                    isPasswordError.value = false
                },
                label = { Text(stringResource(id = R.string.password)) },
                placeholder = { Text(stringResource(id = R.string.password)) },
                visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password,imeAction = ImeAction.Next),
                trailingIcon = {
                    val image = if (passwordVisibility.value)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = {
                        passwordVisibility.value = !passwordVisibility.value
                    }) {
                        Icon(imageVector = image, "")
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
            )

            if (isPasswordError.value) {
                Text(
                    text = stringResource(id = R.string.password_short),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }

            TextField(
                singleLine = true,
                value = confirmPassword.value,
                onValueChange = {
                    confirmPassword.value = it
                    isPasswordConfirmError.value = false
                },
                label = { Text(stringResource(id = R.string.confirm_password)) },
                placeholder = { Text(stringResource(id = R.string.confirm_password)) },
                visualTransformation = if (confirmPasswordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password,imeAction = ImeAction.Next),
                trailingIcon = {
                    val image = if (confirmPasswordVisibility.value)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = {
                        confirmPasswordVisibility.value = !confirmPasswordVisibility.value
                    }) {
                        Icon(imageVector = image, "")
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
                keyboardActions = KeyboardActions(
                    onNext = {focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            if (isPasswordConfirmError.value) {
                Text(
                    text = stringResource(id = R.string.passwords_bot_matches),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }

            if (isError.value) {
                Text(
                    text = stringResource(id = R.string.cant_login),
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(start = 20.dp, top = 12.dp),
                )
            }

            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        if (!isValidEmail(login.value)) {
                            isEmailError.value = true
                        }
                        if (name.value.length <= 2)
                            isLoginError.value = true
                        if (password.value.length <= 3)
                            isPasswordError.value = true
                        if (password.value.isNotEmpty() && password.value != confirmPassword.value)
                            isPasswordConfirmError.value = true

                        if (isValidEmail(login.value) && name.value.length > 2 && password.value.length > 3 && password.value == confirmPassword.value) {
                            registrationViewModel.onEvent(
                                RegistrationEvent.CreateAccount(
                                    password.value,
                                    login.value,
                                    name.value
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(top = 16.dp, start = 32.dp, end = 32.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text(text = stringResource(id = R.string.create_account), color = Color.White)
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Divider(
                    color = MaterialTheme.colors.secondary,
                    thickness = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = stringResource(id = R.string.or),
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colors.secondary,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Divider(
                    color = MaterialTheme.colors.secondary,
                    thickness = 2.dp,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
            }

            val isGoogleIn = remember { mutableStateOf(false) }

            if (isGoogleIn.value) {
                init(LocalContext.current)
                val signInIntent = googleSignInClient.signInIntent
                authResultLauncher.launch(signInIntent)
                isGoogleIn.value = false
            }

            SignInGoogleButton {
                isGoogleIn.value = true
            }

            Divider(
                color = MaterialTheme.colors.secondary,
                thickness = 1.dp,
                modifier = Modifier.padding(top = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Row(Modifier.align(Alignment.Center)) {
                    Text(text = stringResource(id = R.string.have_account), color = MaterialTheme.colors.secondary)
                    Text(
                        text = stringResource(id = R.string.login_here),
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.clickable {
                            onSignUp()
                        }
                    )
                }
            }
        }
        Text(
            text = stringResource(id = R.string.comtimue),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clickable { onSkip() }
                .padding(bottom = 32.dp),
            color = MaterialTheme.colors.primary,
            fontSize = 16.sp
        )
    }
    BackHandler(enabled = true) {
        onSignUp()
    }
}

@Composable
fun SignInGoogleButton(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Surface(
            modifier = Modifier
                .clickable(
                    onClick = onClick
                )
                .align(Alignment.Center),
            border = BorderStroke(width = 1.dp, color = Color.LightGray),
            color = MaterialTheme.colors.surface
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(
                    start = 6.dp,
                    end = 6.dp,
                    top = 6.dp,
                    bottom = 6.dp
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.googlefaviconlogo),
                    contentDescription = "Google Login",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.sign_in_with_google), color = MaterialTheme.colors.secondary
                )

                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

fun isValidEmail(target: CharSequence?): Boolean {
    return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
}