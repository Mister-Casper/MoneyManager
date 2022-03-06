package com.sgcdeveloper.moneymanager.presentation.main

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import javax.inject.Inject


@HiltViewModel
open class MainViewModel
@Inject constructor(
    private val app: Application,
    private val appPreferencesHelper: AppPreferencesHelper,
) : AndroidViewModel(app) {

    val isDarkTheme = mutableStateOf(appPreferencesHelper.getIsDarkTheme())
    val firstDayOfWeek = mutableStateOf(appPreferencesHelper.getFirstDayOfWeek())
    val defaultStartupScreen = mutableStateOf(appPreferencesHelper.getStartupScreen())

    var isShowSelectFirstDayDialog by mutableStateOf(false)
    var isShowSelectStartupScreenDialog by mutableStateOf(false)
    var isShowStartupTransactionTypeDialog by mutableStateOf(false)

    fun setIsDark(isDark: Boolean) {
        isDarkTheme.value = isDark
        appPreferencesHelper.setIsDarkTheme(isDark)
    }

    fun setFirstDayOfWeek(firstDayOfWeek:DayOfWeek){
        this.firstDayOfWeek.value = firstDayOfWeek
        appPreferencesHelper.setFirstDayOfWeek(firstDayOfWeek)
    }

    fun setStartupScreen(startupScreen:BottomMoneyManagerNavigationScreens){
        this.defaultStartupScreen.value = startupScreen
        appPreferencesHelper.setStartupScreen(startupScreen)
    }

}
