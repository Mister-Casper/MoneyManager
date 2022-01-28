package com.sgcdeveloper.moneymanager.presentation.ui.registration

import android.content.Intent

open class GoogleSignInEvent(
    private val intent: Intent,
    private val onSuccess: (isNewUser:Boolean) -> Unit,
    private val onFail: () -> Unit
) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): Triple<Intent, (isBewUser:Boolean) -> Unit, () -> Unit>? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            Triple(intent, onSuccess, onFail)
        }
    }

    fun peekContent(): Triple<Intent, (isBewUser:Boolean) -> Unit, () -> Unit> = Triple(intent, onSuccess, onFail)
}