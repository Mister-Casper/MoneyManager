package com.sgcdeveloper.moneymanager.data.prefa

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import javax.inject.Inject

class AppPreferencesHelper @Inject constructor(context: Context, private val defaultSettings: DefaultSettings) {
    private var prefs: SharedPreferences = context.getSharedPreferences(this.javaClass.name, MODE_PRIVATE)

    fun getLoginStatus(): LoginStatus {
        return LoginStatus.values()[prefs.getInt(LOGIN_STATUS,defaultSettings.loginStatus.ordinal)]
    }

    fun setLoginStatus(loginStatus:LoginStatus) {
        prefs.edit().putInt(LOGIN_STATUS,loginStatus.ordinal).apply()
    }

    companion object {
        private const val LOGIN_STATUS = "LOGIN_STATUS"
    }
}