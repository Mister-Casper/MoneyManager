package com.sgcdeveloper.moneymanager.presentation.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.processphoenix.ProcessPhoenix
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.prefa.LoginStatus
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
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

    val userName = appPreferencesHelper.getUserNAme()

    fun signOut(navController: NavController) {
        val auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("241755459365-s71ite0jght8evhihhu96kijdvu95sh0.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(app, gso)
        auth.signOut()
        googleSignInClient.signOut()

        syncHelper.syncServerData(true) {
            runBlocking {
                appPreferencesHelper.setLoginStatus(LoginStatus.Registering)
                appPreferencesHelper.setUserPassword(false)
                appPreferencesHelper.setDefaultWalletId(-1L)
                appPreferencesHelper.setLastSyncTime(0L)
                appPreferencesHelper.setUserName("")
                moneyManagerRepository.deleteAllWallets()
                moneyManagerRepository.deleteAllTransactions()
                navController.popBackStack(Screen.SignUp.route, true)
                WalletSingleton.setWallet(null)
                ProcessPhoenix.triggerRebirth(app)
            }
        }
    }
}