package com.sgcdeveloper.moneymanager.presentation.ui.registration

sealed class RegistrationEvent {
    class CreateAccount(val password: String, val email: String, val name: String) : RegistrationEvent()
    class SignIn(val password: String, val email: String) : RegistrationEvent()

    object Skip : RegistrationEvent()
}