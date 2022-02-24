package com.sgcdeveloper.moneymanager.presentation.main

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
open class DarkThemeViewModel @Inject constructor(
    private val app: Application,
    private val appPreferencesHelper: AppPreferencesHelper,
) : AndroidViewModel(app) {

    val isDarkTheme = mutableStateOf(appPreferencesHelper.getIsDarkTheme())

    fun setIsDark(isDark:Boolean){
        isDarkTheme.value = isDark
        appPreferencesHelper.setIsDarkTheme(isDark)
    }

}
