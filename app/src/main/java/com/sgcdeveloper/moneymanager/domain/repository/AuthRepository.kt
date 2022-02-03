package com.sgcdeveloper.moneymanager.domain.repository

import com.sgcdeveloper.moneymanager.presentation.ui.registration.RegistrationEvent

interface AuthRepository {
    fun signIn(registrationEvent: RegistrationEvent.SignIn, onResult: (Boolean,String) -> Unit)

    fun signUp(registrationEvent: RegistrationEvent.CreateAccount, onResult: (Boolean,String) -> Unit)
}