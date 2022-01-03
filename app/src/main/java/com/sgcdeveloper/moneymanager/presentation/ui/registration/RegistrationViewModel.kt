package com.sgcdeveloper.moneymanager.presentation.ui.registration

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.prefa.LoginStatus
import com.sgcdeveloper.moneymanager.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
open class RegistrationViewModel @Inject constructor(
    private val app: Application,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val authRepository: AuthRepository
) : AndroidViewModel(app) {

    val loginStatus = mutableStateOf(appPreferencesHelper.getLoginStatus())
    val isSignInError = mutableStateOf(false)

    fun onEvent(registrationEvent: RegistrationEvent) {
        when (registrationEvent) {
            is RegistrationEvent.SignIn -> {
                authRepository.signIn(registrationEvent) {
                    if (it) {
                        loginStatus.value = LoginStatus.None
                        appPreferencesHelper.setLoginStatus(LoginStatus.None)
                    } else {
                        isSignInError.value = true
                    }
                }
            }
            is RegistrationEvent.CreateAccount -> {
                authRepository.signUp(registrationEvent) {
                    if (it) {
                        loginStatus.value = LoginStatus.Initing
                        appPreferencesHelper.setLoginStatus(LoginStatus.Initing)
                    } else {
                        isSignInError.value = true
                    }
                }
            }
            is RegistrationEvent.Skip ->{
                loginStatus.value = LoginStatus.Initing
                appPreferencesHelper.setLoginStatus(LoginStatus.Initing)
            }
        }
    }
}
