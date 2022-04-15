package com.sgcdeveloper.moneymanager.presentation.main

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.util.CSVCreator
import com.sgcdeveloper.moneymanager.domain.util.ExcelCreator
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
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
    private val excelCreator: ExcelCreator
) : AndroidViewModel(app) {

    val isDarkTheme = mutableStateOf(appPreferencesHelper.getIsDarkTheme())
    val firstDayOfWeek = mutableStateOf(appPreferencesHelper.getFirstDayOfWeek())
    val defaultStartupScreen = mutableStateOf(appPreferencesHelper.getStartupScreen())
    val defaultStartupTransactionType = mutableStateOf(appPreferencesHelper.getStartupTransactionType())

    var isShowSelectFirstDayDialog by mutableStateOf(false)
    var isShowSelectStartupScreenDialog by mutableStateOf(false)
    var isShowStartupTransactionTypeDialog by mutableStateOf(false)

    var csvPath by mutableStateOf(Uri.EMPTY)
    var excelPath by mutableStateOf(Uri.EMPTY)

    fun isExistRates(): Boolean = runBlocking { return@runBlocking moneyManagerRepository.getRatesOnce().isNotEmpty() }

    fun setIsDark(isDark: Boolean) {
        isDarkTheme.value = isDark
        appPreferencesHelper.setIsDarkTheme(isDark)
    }

    fun setFirstDayOfWeek(firstDayOfWeek: DayOfWeek) {
        this.firstDayOfWeek.value = firstDayOfWeek
        appPreferencesHelper.setFirstDayOfWeek(firstDayOfWeek)
    }

    fun setStartupScreen(startupScreen: BottomMoneyManagerNavigationScreens) {
        this.defaultStartupScreen.value = startupScreen
        appPreferencesHelper.setStartupScreen(startupScreen)
    }

    fun setStartupTransactionType(startupTransactionType: TransactionType) {
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
}
