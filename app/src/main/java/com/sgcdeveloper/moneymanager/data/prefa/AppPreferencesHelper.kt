package com.sgcdeveloper.moneymanager.data.prefa

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import com.sgcdeveloper.moneymanager.util.Date
import com.sgcdeveloper.moneymanager.util.ReviewStatus
import com.sgcdeveloper.moneymanager.util.gson
import java.time.DayOfWeek
import javax.inject.Inject

class AppPreferencesHelper @Inject constructor(context: Context, private val defaultSettings: DefaultSettings) {
    private var prefs: SharedPreferences = context.getSharedPreferences(this.javaClass.name, MODE_PRIVATE)

    fun getLoginStatus(): LoginStatus {
        return LoginStatus.values()[prefs.getInt(LOGIN_STATUS,defaultSettings.loginStatus.ordinal)]
    }

    fun setLoginStatus(loginStatus:LoginStatus) {
        prefs.edit().putInt(LOGIN_STATUS,loginStatus.ordinal).apply()
    }

    fun getUserNAme():String{
        return prefs.getString(USER_NAME,"")!!
    }

    fun setUserName(userName:String){
        prefs.edit().putString(USER_NAME,userName).apply()
    }

    fun getUserPassword():Boolean{
        return prefs.getBoolean(USER_PASSWORD,false)
    }

    fun setUserPassword(password:Boolean){
        prefs.edit().putBoolean(USER_PASSWORD,password).apply()
    }

    fun getIsDarkTheme():Boolean{
        return prefs.getBoolean(IS_DARK_THEME,true)
    }

    fun setIsDarkTheme(isDark:Boolean){
        prefs.edit().putBoolean(IS_DARK_THEME,isDark).apply()
    }

    fun getIsOld():Boolean{
        return prefs.getBoolean(IS_OLD,false)
    }

    fun setIsOld(isDark:Boolean){
        prefs.edit().putBoolean(IS_OLD,isDark).apply()
    }

    fun getDefaultCurrency():Currency?{
        val json = prefs.getString(DEFAULT_CURRENCY, null) ?: return null
        return gson.fromJson(json, Currency::class.java)
    }

    fun setDefaultCurrency(currency: Currency){
        val json = gson.toJson(currency)
        prefs.edit().putString(DEFAULT_CURRENCY, json).apply()
    }

    fun getDefaultWalletId():Long{
        return prefs.getLong(DEFAULT_WALLET_ID,-1L)
    }

    fun setDefaultWalletId(walletId:Long){
        prefs.edit().putLong(DEFAULT_WALLET_ID,walletId).apply()
    }

    fun getLastSyncTime(): Long {
        return prefs.getLong(LAST_SYNC_TIME,0L)
    }

    fun setLastSyncTime(time: Long) {
        prefs.edit().putLong(LAST_SYNC_TIME,time).apply()
    }

    fun getFirstDayOfWeek(): DayOfWeek {
        return DayOfWeek.of(prefs.getInt(FIRST_DAY_OF_WEEK,defaultSettings.firstDayOfWeek))
    }

    fun setFirstDayOfWeek(day:DayOfWeek) {
        prefs.edit().putInt(FIRST_DAY_OF_WEEK,day.value).apply()
    }

    fun getStartupScreen(): BottomMoneyManagerNavigationScreens {
        return BottomMoneyManagerNavigationScreens.of(prefs.getString(STARTUP_SCREEN,defaultSettings.defaultScreen)!!)
    }

    fun setStartupScreen(startupScreen:BottomMoneyManagerNavigationScreens) {
        prefs.edit().putString(STARTUP_SCREEN,startupScreen.route).apply()
    }

    fun getStartupTransactionType(): TransactionType {
        return TransactionType.getByOrdinal(prefs.getInt(STARTUP_TRANSACTION_TYPE,defaultSettings.defaultTransactionType.ordinal))
    }

    fun setStartupTransactionType(type:TransactionType) {
        prefs.edit().putInt(STARTUP_TRANSACTION_TYPE,type.ordinal).apply()
    }

    fun getFirstTimeOpen(): Date {
        return Date(prefs.getLong(FIRST_OPEN_TIME,-1))
    }

    fun setFirstTimeOpen(firstOpenDate:Date){
        prefs.edit().putLong(FIRST_OPEN_TIME,firstOpenDate.epochMillis).apply()
    }

    fun getTimesOpen(): Int {
        return prefs.getInt(TIMES_OPEN,0)
    }

    fun setTimesOpen(timesOpen:Int){
        prefs.edit().putInt(TIMES_OPEN,timesOpen).apply()
    }

    fun getReviewStatus(): ReviewStatus {
        return ReviewStatus.values().find { it.id == prefs.getInt(REVIEW_STATUS,0) }!!
    }

    fun setReviewStatus(reviewStatus:ReviewStatus){
        prefs.edit().putInt(REVIEW_STATUS,reviewStatus.id).apply()
    }


    companion object {
        const val LOGIN_STATUS = "LOGIN_STATUS"
        const val USER_NAME = "USER_NAME"
        const val DEFAULT_CURRENCY = "DEFAULT_CURRENCY"
        const val DEFAULT_WALLET_ID = "WALLET_ID"
        const val LAST_SYNC_TIME = "LAST_SYNC_TIME"
        const val IS_DARK_THEME = "IS_DARK_THEME"
        const val USER_PASSWORD = "USER_PASSWORD"
        const val IS_OLD = "IS_OLD"
        const val FIRST_DAY_OF_WEEK = "FIRST_DAY_OF_WEEK"
        const val STARTUP_SCREEN = "STARTUP_SCREEN"
        const val STARTUP_TRANSACTION_TYPE = "STARTUP_TRANSACTION_TYPE"
        const val FIRST_OPEN_TIME = "FIRST_OPEN_TIME"
        const val TIMES_OPEN = "TIMES_OPEN"
        const val REVIEW_STATUS = "REVIEW_STATUS"
    }
}