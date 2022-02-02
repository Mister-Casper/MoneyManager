package com.sgcdeveloper.moneymanager.presentation.ui.registration

import android.app.Application
import android.text.TextUtils
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.prefa.LoginStatus
import com.sgcdeveloper.moneymanager.domain.repository.AuthRepository
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.util.Network.checkInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject


@HiltViewModel
open class RegistrationViewModel @Inject constructor(
    private val app: Application,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val authRepository: AuthRepository
) : AndroidViewModel(app) {

    val isSignInError = mutableStateOf(false)
    val isInternetConnection = mutableStateOf(true)

    private val _onGoogleSignIn: MutableLiveData<GoogleSignInEvent> = MutableLiveData()
    val onGoogleSignIn: LiveData<GoogleSignInEvent> = _onGoogleSignIn

    val name = mutableStateOf("")
    val login = mutableStateOf("")

    val password = mutableStateOf("")
    val passwordVisibility = mutableStateOf(false)

    val confirmPassword = mutableStateOf("")
    val confirmPasswordVisibility = mutableStateOf(false)

    val isEmailError = mutableStateOf(false)
    val isLoginError = mutableStateOf(false)
    val isPasswordError = mutableStateOf(false)
    val isPasswordConfirmError = mutableStateOf(false)

    val navigationRoute = mutableStateOf("")

    private var googleSignInClient: GoogleSignInClient

    init {
        val currencies = mutableListOf<String>()
        Currency.getAvailableCurrencies().forEach { currency ->
            currencies.add(currency.currencyCode + " - " + currency.displayName)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(app.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(app, gso)

        updateLogInStatus(appPreferencesHelper.getLoginStatus())
    }

    fun onEvent(registrationEvent: RegistrationEvent) {
        when (registrationEvent) {
            is RegistrationEvent.SignIn -> {
                authRepository.signIn(registrationEvent) {
                    if (checkInternetConnection()) {
                        if (it) {
                            updateLogInStatus(LoginStatus.None)
                        } else {
                            isSignInError.value = true
                        }
                    }
                }
            }
            is RegistrationEvent.CreateAccount -> {
                if (!isValidEmail(login.value)) {
                    isEmailError.value = true
                } else if (name.value.length <= 2)
                    isLoginError.value = true
                else if (password.value.length <= 3)
                    isPasswordError.value = true
                else if (password.value.isNotEmpty() && password.value != confirmPassword.value)
                    isPasswordConfirmError.value = true
                else {
                    authRepository.signUp(registrationEvent) {
                        if (checkInternetConnection()) {
                            if (it) {
                                updateLogInStatus(LoginStatus.Initing)
                            } else {
                                isSignInError.value = true
                            }
                        }
                    }
                }
            }
            is RegistrationEvent.Skip -> {
                updateLogInStatus(LoginStatus.Initing)
            }
            is RegistrationEvent.SignInWithGoogle -> {
                val signInIntent = googleSignInClient.signInIntent
                _onGoogleSignIn.value = GoogleSignInEvent(signInIntent, { isNewUser->
                    if (isNewUser)
                        updateLogInStatus(LoginStatus.Initing)
                    else
                        updateLogInStatus(LoginStatus.None)
                }, {
                    isSignInError.value = true
                })
            }
            is RegistrationEvent.ChangeName -> {
                name.value = registrationEvent.newName
                isLoginError.value = false
            }
            is RegistrationEvent.ChangeLogin -> {
                login.value = registrationEvent.newLogin
                isEmailError.value = false
            }
            is RegistrationEvent.ChangePassword -> {
                password.value = registrationEvent.newPassword
                isPasswordError.value = false
            }
            is RegistrationEvent.ChangeConfirmPassword -> {
                confirmPassword.value = registrationEvent.newPassword
                isPasswordConfirmError.value = false
            }
            is RegistrationEvent.MoveToSignIn -> {
                navigationRoute.value = Screen.SignIn.route
            }
            is RegistrationEvent.MoveToSignUp -> {
                navigationRoute.value = Screen.SignUp.route
                clear()
            }
        }
    }

    private fun clear() {
        isSignInError.value = false
        isInternetConnection.value = true
        name.value = ""
        login.value = ""
        password.value = ""
        passwordVisibility.value = false
        confirmPassword.value = ""
        confirmPasswordVisibility.value = false
        isEmailError.value = false
        isLoginError.value = false
        isPasswordError.value = false
        isPasswordConfirmError.value = false
    }

    private fun updateLogInStatus(loginStatus: LoginStatus) {
        if (loginStatus == LoginStatus.Initing) {
            navigationRoute.value = Screen.Init.route
        } else if (loginStatus == LoginStatus.None) {
            navigationRoute.value = Screen.MoneyManagerScreen.route
        }
        appPreferencesHelper.setLoginStatus(loginStatus)
    }

    private fun checkInternetConnection(): Boolean {
        val isConnected = app.checkInternetConnection()
        if (!isConnected)
            isSignInError.value = false
        isInternetConnection.value = isConnected
        return isConnected
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}
