package com.sgcdeveloper.moneymanager.presentation.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.red
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.PasswordInputField
import com.sgcdeveloper.moneymanager.presentation.ui.composables.SignInGoogleButton
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DeleteDialog
import com.sgcdeveloper.moneymanager.presentation.ui.registration.RegistrationEvent
import com.sgcdeveloper.moneymanager.presentation.ui.registration.RegistrationViewModel

@OptIn(ExperimentalCoilApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun AccountSettings(
    navController: NavController,
    accountSettingsViewModel: AccountSettingsViewModel,
    registrationViewModel: RegistrationViewModel
) {
    val user = FirebaseAuth.getInstance().currentUser
    val url = user?.photoUrl?.toString()
    var isShowDeleteUSerDialog by remember { mutableStateOf(false) }
    var isShowUpdatePasswordDialog by remember { mutableStateOf(false) }

    if (isShowDeleteUSerDialog) {
        DeleteDialog(
            massage = stringResource(
                id = R.string.are_u_sure_delte_user
            ), onDelete = {
                accountSettingsViewModel.deleteUser()
            }) {
            isShowDeleteUSerDialog = false
        }
    }

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(top = 16.dp, bottom = 16.dp)
            ) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(32.dp)
                        .padding(start = 12.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
                Text(
                    text = stringResource(id = R.string.account_settings),
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 12.dp),
                    color = MaterialTheme.colors.onBackground
                )
            }
            if (user != null) {
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
                                contentDescription = "",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxSize(),
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                    Column(
                        Modifier
                            .weight(2f)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = user.email!!,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colors.onBackground
                        )
                        Text(
                            text = accountSettingsViewModel.userName,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colors.onBackground
                        )
                        Text(
                            text = stringResource(id = R.string.sign_out),
                            color = red,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .clickable {
                                    accountSettingsViewModel.signOut(navController)
                                }
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            } else {
                SignInGoogleButton {
                    registrationViewModel.onEvent(RegistrationEvent.SignInWithGoogle)
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 32.dp, end = 32.dp)
            ) {
                if (user != null && user.photoUrl == null) {
                    Text(
                        text = stringResource(id = R.string.password_change),
                        fontSize = 22.sp,
                        color = MaterialTheme.colors.onSurface
                    )

                    val context = LocalContext.current

                    val keyboardController = LocalSoftwareKeyboardController.current
                    val focusManager = LocalFocusManager.current
                    var password by remember { mutableStateOf("") }
                    var confirmPassword by remember { mutableStateOf("") }
                    val passwordVisibility = remember { mutableStateOf(true) }
                    val confirmPasswordVisibility = remember { mutableStateOf(true) }
                    var isPasswordError by remember { mutableStateOf(false) }
                    var isPasswordConfirmError by remember { mutableStateOf(false) }

                    PasswordInputField(
                        password,
                        {
                            password = it
                            isPasswordError = false
                            isPasswordConfirmError = false
                        },
                        stringResource(id = R.string.password),
                        isPasswordError,
                        stringResource(id = R.string.password_short),
                        focusManager,
                        passwordVisibility,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        })
                    )

                    PasswordInputField(
                        confirmPassword,
                        {
                            confirmPassword = it
                            isPasswordError = false
                            isPasswordConfirmError = false
                        },
                        stringResource(id = R.string.confirm_password),
                        isPasswordConfirmError,
                        stringResource(id = R.string.passwords_bot_matches),
                        focusManager,
                        confirmPasswordVisibility,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()
                        })
                    )

                    Button(
                        onClick = {
                            if (password.length <= 3)
                                isPasswordError = true
                            else if (password.isNotEmpty() && password != confirmPassword)
                                isPasswordConfirmError = true
                            else
                                user.updatePassword(password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.password_changed),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.cant_change_password),
                                                Toast.LENGTH_LONG
                                            ).show()
                                    }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp)
                            .fillMaxSize()
                    ) {
                        Text(text = stringResource(id = R.string.change_password), color = white, fontSize = 18.sp)
                    }
                }
                Button(
                    onClick = { isShowDeleteUSerDialog = true },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                        .fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = red)
                ) {
                    Text(text = stringResource(id = R.string.delete_user), color = white, fontSize = 18.sp)
                }
            }
        }
    }
}