package com.sgcdeveloper.moneymanager.util

import android.content.Context
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
import kotlinx.coroutines.*
import java.time.DayOfWeek
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
        val wallets = userDocument.get("wallets") as List<MutableMap<String, Any>>
        val transactions = userDocument.get("transactions") as List<MutableMap<String, Any>>
        GlobalScope.launch {
            moneyManagerRepository.insertWallets(wallets.map { wallet -> WalletEntry.getWalletByHashMap(wallet) })
            moneyManagerRepository.insertTransactions(transactions.map { task -> TransactionEntry.getTaskByHashMap(task) })
        }
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
        val firstDayOfWeek = userDocument.getLong(AppPreferencesHelper.FIRST_DAY_OF_WEEK)
        if (firstDayOfWeek != null)
            appPreferencesHelper.setFirstDayOfWeek(DayOfWeek.of(firstDayOfWeek.toInt()))
        val isDarkTHeme = userDocument.getBoolean(AppPreferencesHelper.IS_DARK_THEME)
        if (isDarkTHeme != null)
            appPreferencesHelper.setIsDarkTheme(isDarkTHeme)
        val isOld = userDocument.getBoolean(AppPreferencesHelper.IS_OLD)
        if (isOld != null)
            appPreferencesHelper.setIsOld(isOld)
        return false
    }

    suspend fun syncServerData(onFinish: () -> Unit = {}) =
        CoroutineScope(Dispatchers.IO).async {
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val docRef: DocumentReference = db.collection("users").document(user.uid)
                val time = Date(LocalDateTime.now()).epochMillis

                appPreferencesHelper.setLastSyncTime(time)
                val settingsData = hashMapOf(
                    AppPreferencesHelper.LAST_SYNC_TIME to time,
                    AppPreferencesHelper.USER_NAME to appPreferencesHelper.getUserNAme(),
                    AppPreferencesHelper.LOGIN_STATUS to Gson().toJson(appPreferencesHelper.getLoginStatus()),
                    AppPreferencesHelper.DEFAULT_CURRENCY to Gson().toJson(appPreferencesHelper.getDefaultCurrency()),
                    AppPreferencesHelper.DEFAULT_WALLET_ID to appPreferencesHelper.getDefaultWalletId(),
                    AppPreferencesHelper.FIRST_DAY_OF_WEEK to appPreferencesHelper.getFirstDayOfWeek().value,
                    AppPreferencesHelper.IS_DARK_THEME to appPreferencesHelper.getIsDarkTheme(),
                    AppPreferencesHelper.IS_OLD to appPreferencesHelper.getIsOld(),
                    "wallets" to getWallets(),
                    "transactions" to getTransactions()
                )
                docRef.set(settingsData)
                onFinish()

            }
        }.await()

    private suspend fun getWallets(): List<MutableMap<String, Any>> {
        val list: MutableList<MutableMap<String, Any>> = ArrayList()
        moneyManagerRepository.getAsyncWallets().forEach { wallet ->
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