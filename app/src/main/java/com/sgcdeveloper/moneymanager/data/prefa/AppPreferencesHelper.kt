package com.sgcdeveloper.moneymanager.data.prefa

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import javax.inject.Inject

class AppPreferencesHelper @Inject constructor(context: Context, private val defaultSettings: DefaultSettings) {
    private var prefs: SharedPreferences = context.getSharedPreferences(this.javaClass.name, MODE_PRIVATE)

    fun getIsSigned(): Boolean {
        return prefs.getBoolean(IS_SIGNED,defaultSettings.isSigned)
    }

    fun setIsSigned(isSigned:Boolean) {
        prefs.edit().putBoolean(IS_SIGNED,isSigned).apply()
    }

    companion object {
        private const val IS_SIGNED = "IS_SIGNED"
    }
}