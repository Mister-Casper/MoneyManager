package com.sgcdeveloper.moneymanager.util

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry
import com.sgcdeveloper.moneymanager.data.db.entry.RateEntry
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.prefa.LoginStatus
import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import kotlinx.coroutines.*
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.*
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
        val rates =
            if (userDocument.get("rates") != null) userDocument.get("rates") as List<MutableMap<String, Any>> else Collections.emptyList()
        val budgets =
            if (userDocument.get("budgets") != null) userDocument.get("budgets") as List<MutableMap<String, Any>> else Collections.emptyList()
        GlobalScope.launch {
            moneyManagerRepository.insertWallets(wallets.map { wallet -> WalletEntry.getWalletByHashMap(wallet) })
            moneyManagerRepository.insertTransactions(transactions.map { task -> TransactionEntry.getTaskByHashMap(task) })
            moneyManagerRepository.insertRates(rates.map { rate -> RateEntry.getRateByHashMap(rate) })
            moneyManagerRepository.insertBudgets(budgets.map { budget -> BudgetEntry.getBudgetByHashMap(budget) })
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
        val firstDay = userDocument.getLong(AppPreferencesHelper.FIRST_DAY_OF_WEEK)
        if (firstDay != null)
            appPreferencesHelper.setFirstDayOfWeek(DayOfWeek.of(firstDay.toInt()))
        val startupScreen = userDocument.getString(AppPreferencesHelper.STARTUP_SCREEN)
        if (startupScreen != null)
            appPreferencesHelper.setStartupScreen(BottomMoneyManagerNavigationScreens.of(startupScreen))
        val startupTransactionType = userDocument.getLong(AppPreferencesHelper.STARTUP_TRANSACTION_TYPE)
        if (startupTransactionType != null)
            appPreferencesHelper.setStartupTransactionType(TransactionType.getByOrdinal(startupTransactionType.toInt()))
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
                    AppPreferencesHelper.FIRST_DAY_OF_WEEK to appPreferencesHelper.getFirstDayOfWeek().value,
                    AppPreferencesHelper.STARTUP_SCREEN to appPreferencesHelper.getStartupScreen().route,
                    AppPreferencesHelper.STARTUP_TRANSACTION_TYPE to appPreferencesHelper.getStartupTransactionType().ordinal,
                    "wallets" to getWallets(),
                    "transactions" to getTransactions(),
                    "rates" to getRate(),
                    "budgets" to getBudgets()
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

    private suspend fun getRate(): List<MutableMap<String, Any>> {
        val list: MutableList<MutableMap<String, Any>> = ArrayList()
        moneyManagerRepository.getRatesOnce().forEach { rate ->
            list.add(rate.toObject())
        }
        return list
    }

    private suspend fun getBudgets(): List<MutableMap<String, Any>> {
        val list: MutableList<MutableMap<String, Any>> = ArrayList()
        moneyManagerRepository.getAsyncWBudgets().forEach { rate ->
            list.add(rate.toObject())
        }
        return list
    }

}