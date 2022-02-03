package com.sgcdeveloper.moneymanager.data.prefa

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.domain.model.Currency
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

    fun getDefaultCurrency():Currency{
        val json = prefs.getString(DEFAULT_CURRENCY, null)
        return Gson().fromJson(json, Currency::class.java)
    }

    fun setDefaultCurrency(currency: Currency){
        val json = Gson().toJson(currency)
        prefs.edit().putString(DEFAULT_CURRENCY, json).apply()
    }

    companion object {
        private const val LOGIN_STATUS = "LOGIN_STATUS"
        private const val USER_NAME = "USER_NAME"
        private const val DEFAULT_CURRENCY = "DEFAULT_CURRENCY"
    }
}