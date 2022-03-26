package com.sgcdeveloper.moneymanager.presentation.ui.budgetManager

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.BaseBudget
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.use_case.GetBudgetsUseCase
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod
import com.sgcdeveloper.moneymanager.util.Date
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TimeIntervalBudgetManagerViewModel @Inject constructor(
    private val app: Application,
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val appPreferencesHelper:AppPreferencesHelper
) : AndroidViewModel(app) {

    val description = mutableStateOf("")

    var budgets = mutableStateListOf<BaseBudget.BudgetItem>()
    lateinit var timeIntervalController: TimeIntervalController
    lateinit var period: BudgetPeriod
    var loadBudgetsJob: Job? = null

    fun loadBudgets(period: BudgetPeriod) {
        timeIntervalController = when (period) {
            BudgetPeriod.Daily -> {
                TimeIntervalController.DailyController()
            }
            BudgetPeriod.Weekly -> {
                val date = getStartDate(Date(LocalDateTime.now()), appPreferencesHelper.getFirstDayOfWeek())
                TimeIntervalController.WeeklyController(date)
            }
            BudgetPeriod.Monthly -> {
                TimeIntervalController.MonthlyController()
            }
            BudgetPeriod.Quarterly -> {
                TimeIntervalController.QuarterlyController()
            }
            BudgetPeriod.Yearly -> {
                TimeIntervalController.YearlyController()
            }
        }
        description.value = timeIntervalController.getDescription()
        this.period = period
        loadBudgetsJob?.cancel()
        loadBudgetsJob = viewModelScope.launch {
            budgets.clear()
            budgets.addAll(
                getBudgetsUseCase(
                    firstDate = timeIntervalController.getStartDate(),
                    period = period
                ) as List<BaseBudget.BudgetItem>
            )
        }
    }

    private fun getStartDate(now: Date, firstDay: DayOfWeek): Date {
        val dif = if (firstDay == now.getAsLocalDate().dayOfWeek)
            0
        else
            kotlin.math.abs(now.getAsLocalDate().dayOfWeek.value - firstDay.value + 7)
        return Date(now.getAsLocalDate().minusDays(dif.toLong()))
    }

    fun moveBack() {
        timeIntervalController.moveBack()
        description.value = timeIntervalController.getDescription()
        loadBudgetsJob?.cancel()
        loadBudgetsJob = viewModelScope.launch {
            budgets.clear()
            budgets.addAll(
                getBudgetsUseCase(
                    firstDate = timeIntervalController.getStartDate(),
                    period = period
                ) as List<BaseBudget.BudgetItem>
            )
        }
    }

    fun moveNext() {
        timeIntervalController.moveNext()
        description.value = timeIntervalController.getDescription()
        loadBudgetsJob?.cancel()
        loadBudgetsJob = viewModelScope.launch {
            budgets.clear()
            budgets.addAll(
                getBudgetsUseCase(
                    firstDate = timeIntervalController.getStartDate(),
                    period = period
                ) as List<BaseBudget.BudgetItem>
            )
        }
    }
}