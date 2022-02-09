package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.ui.registration.RegistrationEvent
import com.sgcdeveloper.moneymanager.presentation.ui.registration.RegistrationViewModel
@Composable
fun DummyProgress(isShowLoadingDialog: Boolean) {
    if (isShowLoadingDialog){
        Dialog(
            onDismissRequest = { },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colors.background, shape = RoundedCornerShape(8.dp))
            ) {
                Column {
                    CircularProgressIndicator(modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp))
                    Text(text = "Loading...", Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp))
                }
            }
        }
    }
}
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
        keyboardOptions =  KeyboardOptions(keyboardType = KeyboardType.Password,imeAction = ImeAction.Next),
    )
}

@Composable
fun ColumnScope.InputField(
    value: String,
    onValueChange: (it: String) -> Unit,
    label: String,
    isError: Boolean,
    errorText: String,
    focusManager: FocusManager?=null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions =  KeyboardOptions(imeAction = ImeAction.Next)
) {
    TextField(
        singleLine = true,
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(label) },
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 12.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.secondary),
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onNext = { focusManager?.moveFocus(FocusDirection.Down) }
        ),
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation
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

    SignInGoogleButton {
        registrationViewModel.onEvent(RegistrationEvent.SignInWithGoogle)
    }

    Divider(
        color = MaterialTheme.colors.secondary,
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