package com.sgcdeveloper.moneymanager.presentation.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jakewharton.processphoenix.ProcessPhoenix
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.prefa.LoginStatus
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.util.SyncHelper
import com.sgcdeveloper.moneymanager.util.WalletSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
open class AccountSettingsViewModel @Inject constructor(
    private val app: Application,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val syncHelper: SyncHelper,
    private val moneyManagerRepository: MoneyManagerRepository
) : AndroidViewModel(app) {

    val userName =appPreferencesHelper.getUserNAme()

    fun signOut(){
        syncHelper.syncServerData(true) {
            runBlocking {
                WalletSingleton.setWallet(null)
                appPreferencesHelper.setLoginStatus(LoginStatus.Registering)
                appPreferencesHelper.setUserPassword(false)
                appPreferencesHelper.setDefaultWalletId(-1L)
                appPreferencesHelper.setLastSyncTime(0L)
                appPreferencesHelper.setUserName("")
                moneyManagerRepository.deleteAllWallets()
                moneyManagerRepository.deleteAllTransactions()
                ProcessPhoenix.triggerRebirth(app)
            }
        }

    }
}