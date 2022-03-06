package com.sgcdeveloper.moneymanager.presentation.ui.weeklyStatisticScreen

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.DayStatistic
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetWeeklyStatistic
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.util.Date
import com.sgcdeveloper.moneymanager.util.WalletSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class WeeklyStatisticViewModel @Inject constructor(
    private val app: Application,
    private val getWeeklyStatistic: GetWeeklyStatistic,
    private val appPreferencesHelper: AppPreferencesHelper
) : AndroidViewModel(app) {

    val empties = mutableStateOf<List<BarEntry>>(Collections.emptyList())
    val days = mutableStateOf<List<DayStatistic>>(Collections.emptyList())
    val title = mutableStateOf(app.getString(R.string.weekly_spending))
    val timeIntervalDescription = mutableStateOf("")
    val sum = mutableStateOf("")

    var labels = Collections.emptyList<String>()
    var rowColor = blue.toArgb()

    val wallet = mutableStateOf(WalletSingleton.wallet.value!!)
    lateinit var timeIntervalController: TimeIntervalController.WeeklyController
    private var transactionType = TransactionType.Expense

    fun onEvent(weeklyStatisticScreenEvent: WeeklyStatisticScreenEvent) {
        when (weeklyStatisticScreenEvent) {
            is WeeklyStatisticScreenEvent.Init -> {
                timeIntervalController =
                    TimeIntervalController.WeeklyController(
                        getWeeklyStatistic.getStartDate(
                            Date(LocalDate.now()),
                            appPreferencesHelper.getFirstDayOfWeek()
                        )
                    )
                wallet.value = weeklyStatisticScreenEvent.wallet
                transactionType = weeklyStatisticScreenEvent.transactionType
                loadStatistic()
            }
            is WeeklyStatisticScreenEvent.MoveBack -> {
                timeIntervalController.moveBack()
                loadStatistic()
            }
            is WeeklyStatisticScreenEvent.MoveNext -> {
                timeIntervalController.moveNext()
                loadStatistic()
            }
        }
    }

    private fun loadStatistic() {
        viewModelScope.launch {
            val statistic =
                getWeeklyStatistic(
                    app,
                    wallet.value,
                    transactionType,
                    timeIntervalController.startDay,
                    timeIntervalController
                )
            labels = statistic.labels
            rowColor = statistic.rowColor
            empties.value = statistic.dayItems.map { it.entry }
            days.value = statistic.dayItems
            title.value = statistic.title
            sum.value = statistic.sum
            timeIntervalDescription.value = timeIntervalController.getDescription()
        }
    }
}