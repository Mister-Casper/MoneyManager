package com.sgcdeveloper.moneymanager.util

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.prefa.LoginStatus
import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

class SyncHelper @Inject constructor(
    private val appPreferencesHelper: AppPreferencesHelper,
    private val moneyManagerRepository: MoneyManagerRepository,
    private val context: Context
) {

    fun syncLocalData(isAnyway: Boolean = false, isNewUser: (isNew: Boolean) -> Unit = {}) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val docRef: DocumentReference = db.collection("users").document(user.uid)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        val lastUpdate =
                            document.data?.getOrDefault(AppPreferencesHelper.LAST_SYNC_TIME, -1000) as Long
                        if (lastUpdate > appPreferencesHelper.getLastSyncTime() || isAnyway) {
                            if (loadSyncData(document)) {
                                isNewUser(true)
                            } else
                                isNewUser(false)
                        }
                    } else {
                        isNewUser(true)
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadSyncData(userDocument: DocumentSnapshot): Boolean {
        val lastSyncTIme = userDocument.getLong(AppPreferencesHelper.LAST_SYNC_TIME)
        if (lastSyncTIme != null)
            appPreferencesHelper.setLastSyncTime(lastSyncTIme)
        else
            return true
        val loginStatus = userDocument.getString(AppPreferencesHelper.LOGIN_STATUS)
        if (loginStatus != null)
            appPreferencesHelper.setLoginStatus(Gson().fromJson(loginStatus, LoginStatus::class.java))
        val userName = userDocument.getString(AppPreferencesHelper.USER_NAME)
        if (userName != null)
            appPreferencesHelper.setUserName(userName)
        val defaultCurrency = userDocument.getString(AppPreferencesHelper.DEFAULT_CURRENCY)
        if (defaultCurrency != null)
            appPreferencesHelper.setDefaultCurrency(Gson().fromJson(defaultCurrency, Currency::class.java))
        val walletId = userDocument.getLong(AppPreferencesHelper.DEFAULT_WALLET_ID)
        if (walletId != null)
            appPreferencesHelper.setDefaultWalletId(walletId)

        val wallets = userDocument.get("wallets") as List<MutableMap<String, Any>>
        val transactions = userDocument.get("transactions") as List<MutableMap<String, Any>>
        GlobalScope.launch {
            moneyManagerRepository.deleteAllWallets()
            moneyManagerRepository.deleteAllTransactions()

            moneyManagerRepository.insertWallets(wallets.map { wallet -> WalletEntry.getWalletByHashMap(wallet) })
            moneyManagerRepository.insertTransactions(transactions.map { task -> TransactionEntry.getTaskByHashMap(task) })
        }
        return false
    }

    fun syncServerData(isSignOut: Boolean = false) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val docRef: DocumentReference = db.collection("users").document(user.uid)
            val time = Date(LocalDateTime.now()).epochMillis

            GlobalScope.launch {
                appPreferencesHelper.setLastSyncTime(time)
                val settingsData = hashMapOf(
                    AppPreferencesHelper.LAST_SYNC_TIME to time,
                    AppPreferencesHelper.USER_NAME to appPreferencesHelper.getUserNAme(),
                    AppPreferencesHelper.LOGIN_STATUS to Gson().toJson(appPreferencesHelper.getLoginStatus()),
                    AppPreferencesHelper.DEFAULT_CURRENCY to Gson().toJson(appPreferencesHelper.getDefaultCurrency()),
                    AppPreferencesHelper.DEFAULT_WALLET_ID to appPreferencesHelper.getDefaultWalletId(),
                    "wallets" to getWallets(),
                    "transactions" to getTransactions()
                )
                docRef.set(settingsData)

                if (isSignOut) {
                    val auth = FirebaseAuth.getInstance()
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("241755459365-s71ite0jght8evhihhu96kijdvu95sh0.apps.googleusercontent.com")
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    auth.signOut()
                    googleSignInClient.signOut()
                }
            }
        }
    }

    private fun getWallets(): List<MutableMap<String, Any>> {
        val list: MutableList<MutableMap<String, Any>> = ArrayList()
        moneyManagerRepository.getWalletsOnce().forEach { wallet ->
            list.add(wallet.toObject())
        }
        return list
    }

    private suspend fun getTransactions(): List<MutableMap<String, Any>> {
        val list: MutableList<MutableMap<String, Any>> = ArrayList()
        moneyManagerRepository.getTransactionsOnce().forEach { transaction ->
            list.add(transaction.toObject())
        }
        return list
    }

}