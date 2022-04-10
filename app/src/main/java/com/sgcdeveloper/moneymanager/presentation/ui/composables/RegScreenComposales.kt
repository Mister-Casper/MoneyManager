package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.ui.registration.RegistrationEvent
import com.sgcdeveloper.moneymanager.presentation.ui.registration.RegistrationViewModel

@Composable
fun SignInError(isError: Boolean) {
    if (isError) {
        Text(
            text = stringResource(id = R.string.cant_login),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = 20.dp, top = 12.dp),
        )
    }
}

@Composable
fun NoInternetError(isConnectInternet: Boolean) {
    if (!isConnectInternet) {
        Text(
            text = stringResource(id = R.string.check_internet),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = 20.dp, top = 12.dp),
        )
    }
}

@Composable
fun ColumnScope.PasswordInputField(
    value: String,
    onValueChange: (it: String) -> Unit,
    label: String,
    isError: Boolean,
    errorText: String,
    focusManager: FocusManager,
    passwordVisibility: MutableState<Boolean>
) {
    InputField(
        value, onValueChange, label, isError, errorText, focusManager,
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
        visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
    )
}

@Composable
fun ColumnScope.InputField(
    value: String,
    onValueChange: (it: String) -> Unit,
    label: String,
    isError: Boolean,
    errorText: String,
    focusManager: FocusManager? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    padding: Dp = 10.dp,
    keyboardActions: KeyboardActions = KeyboardActions {  }
) {
    TextField(
        singleLine = true,
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(label) },
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 12.dp, start = padding, end = padding)
            .fillMaxWidth(),
        keyboardOptions = keyboardOptions,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardActions = keyboardActions
    )

    if (isError) {
        Text(
            text = errorText,
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

@Composable
fun SignInGoogle(registrationViewModel: RegistrationViewModel) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Divider(
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
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Divider(
            thickness = 2.dp,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
    }

    SignInGoogleButton {
        registrationViewModel.onEvent(RegistrationEvent.SignInWithGoogle)
    }

    Divider(
        thickness = 1.dp,
        modifier = Modifier.padding(top = 16.dp)
    )
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
            color = MaterialTheme.colors.background
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
                    text = stringResource(id = R.string.sign_in_with_google)
                )

                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}