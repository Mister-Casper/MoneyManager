package com.sgcdeveloper.moneymanager.presentation.main

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.util.CSVCreator
import com.sgcdeveloper.moneymanager.domain.util.ExcelCreator
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import com.sgcdeveloper.moneymanager.util.RateUsDialogHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
import javax.inject.Inject


@HiltViewModel
open class MainViewModel
@Inject constructor(
    private val app: Application,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val moneyManagerRepository: MoneyManagerRepository,
    private val csvCreator: CSVCreator,
    private val excelCreator: ExcelCreator,
    private val rateUsDialogHelper: RateUsDialogHelper
) : AndroidViewModel(app) {

    val isAutoReturn = mutableStateOf(appPreferencesHelper.getAutoReturn())
    val isDarkTheme = mutableStateOf(appPreferencesHelper.getIsDarkTheme())
    val firstDayOfWeek = mutableStateOf(appPreferencesHelper.getFirstDayOfWeek())
    val defaultStartupScreen = mutableStateOf(appPreferencesHelper.getStartupScreen())
    val defaultStartupTransactionType = mutableStateOf(appPreferencesHelper.getStartupTransactionType())

    var isShowSelectFirstDayDialog by mutableStateOf(false)
    var isShowSelectStartupScreenDialog by mutableStateOf(false)
    var isShowStartupTransactionTypeDialog by mutableStateOf(false)

    var csvPath by mutableStateOf(Uri.EMPTY)
    var excelPath by mutableStateOf(Uri.EMPTY)
    var isNeedRateUs = rateUsDialogHelper.isNeedShow

    fun isExistRates(): Boolean = runBlocking { return@runBlocking moneyManagerRepository.getRatesOnce().isNotEmpty() }

    fun setIsDark(isDark: Boolean) {
        FirebaseAnalytics.getInstance(app).logEvent("change_theme",null)
        isDarkTheme.value = isDark
        appPreferencesHelper.setIsDarkTheme(isDark)
    }

    fun setFirstDayOfWeek(firstDayOfWeek: DayOfWeek) {
        FirebaseAnalytics.getInstance(app).logEvent("change_first_day",null)
        this.firstDayOfWeek.value = firstDayOfWeek
        appPreferencesHelper.setFirstDayOfWeek(firstDayOfWeek)
    }

    fun setStartupScreen(startupScreen: BottomMoneyManagerNavigationScreens) {
        FirebaseAnalytics.getInstance(app).logEvent("change_startup_screen",null)
        this.defaultStartupScreen.value = startupScreen
        appPreferencesHelper.setStartupScreen(startupScreen)
    }

    fun setStartupTransactionType(startupTransactionType: TransactionType) {
        FirebaseAnalytics.getInstance(app).logEvent("change_startup_type",null)
        this.defaultStartupTransactionType.value = startupTransactionType
        appPreferencesHelper.setStartupTransactionType(startupTransactionType)
    }

    fun saveCSV() {
        viewModelScope.launch {
            csvPath = csvCreator()
        }
    }

    fun saveExcel() {
        viewModelScope.launch {
            excelPath = excelCreator()
        }
    }

    fun rated() {
        rateUsDialogHelper.rated()
    }

    fun setAutoReturn(it: Boolean) {
        FirebaseAnalytics.getInstance(app).logEvent("change_auto_return",null)
        isAutoReturn.value = it
        appPreferencesHelper.setAutoReturn(it)
    }
}
