package com.sgcdeveloper.moneymanager.presentation.ui.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.ui.composables.InputField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegainAccess(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp, bottom = 16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.cancel_icon),
            contentDescription = "cancel",
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier
                .clickable {
                    navController.popBackStack()
                }
                .size(32.dp))
        Column(
            Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.regain_access),
                fontSize = 30.sp,
                color = MaterialTheme.colors.onSurface,
            )
            Text(
                text = stringResource(id = R.string.regain_access_desription),
                Modifier.padding(top = 12.dp),
                color = MaterialTheme.colors.onSurface
            )
            InputField(
                email,
                {
                    email = it
                    isEmailError = false
                },
                stringResource(id = R.string.email_address),
                isEmailError,
                stringResource(id = R.string.email_sent_error),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    keyboardController?.hide()
                }),
                padding = 0.dp
            )
        }
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(0.3f))
            Button(
                onClick = {
                    val auth = FirebaseAuth.getInstance()
                    auth.setLanguageCode(java.util.Locale.getDefault().toLanguageTag())

                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navController.popBackStack()
                            } else {
                                isEmailError = true
                            }
                        }
                },
                Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(32.dp),
            ) {
                androidx.compose.material.Text(text = stringResource(id = R.string.send), color = Color.White)
            }
            Spacer(Modifier.weight(0.3f))
        }
    }
}