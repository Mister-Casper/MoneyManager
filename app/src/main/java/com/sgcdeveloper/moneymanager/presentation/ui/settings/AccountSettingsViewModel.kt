package com.sgcdeveloper.moneymanager.presentation.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.prefa.LoginStatus
import com.sgcdeveloper.moneymanager.util.SyncHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class AccountSettingsViewModel @Inject constructor(
    private val app: Application,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val syncHelper: SyncHelper
) : AndroidViewModel(app) {

    val userName =appPreferencesHelper.getUserNAme()

    fun signOut(){
        syncHelper.syncServerData(true)
        appPreferencesHelper.setLoginStatus(LoginStatus.Registering)
    }
}