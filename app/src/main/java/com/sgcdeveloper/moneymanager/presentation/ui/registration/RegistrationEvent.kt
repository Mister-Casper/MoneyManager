package com.sgcdeveloper.moneymanager.presentation.ui.registration

sealed class RegistrationEvent {
    class CreateAccount(val password: String, val email: String, val name: String) : RegistrationEvent()
    class SignIn(val password: String, val email: String) : RegistrationEvent()
    class ChangeName(val newName:String) : RegistrationEvent()
    class ChangeLogin(val newLogin:String) : RegistrationEvent()
    class ChangePassword(val newPassword:String) : RegistrationEvent()
    class ChangeConfirmPassword(val newPassword:String) : RegistrationEvent()

    object SignInWithGoogle :RegistrationEvent()
    object Skip : RegistrationEvent()
    object MoveToSignUp : RegistrationEvent()
    object MoveToSignIn : RegistrationEvent()
}