package com.sgcdeveloper.moneymanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.sgcdeveloper.moneymanager.domain.repository.AuthRepository
import com.sgcdeveloper.moneymanager.presentation.ui.registration.RegistrationEvent
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun signIn(registrationEvent: RegistrationEvent.SignIn, onResult: (Boolean) -> Unit) {
        if(registrationEvent.email.isEmpty() || registrationEvent.password.isEmpty()) {
            onResult(false)
            return
        }
        auth.signInWithEmailAndPassword(registrationEvent.email, registrationEvent.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

    override fun signUp(registrationEvent: RegistrationEvent.CreateAccount, onResult: (Boolean) -> Unit) {
        if(registrationEvent.email.isEmpty() || registrationEvent.password.isEmpty()) {
            onResult(false)
            return
        }
        auth.createUserWithEmailAndPassword(registrationEvent.email, registrationEvent.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

}