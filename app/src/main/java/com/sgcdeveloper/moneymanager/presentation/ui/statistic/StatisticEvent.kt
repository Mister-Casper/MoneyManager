package com.sgcdeveloper.moneymanager.presentation.ui.statistic

import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController

sealed class StatisticEvent {
    class ChangeTimeInterval(val timeIntervalController: TimeIntervalController) : StatisticEvent()

    object MoveNext : StatisticEvent()
    object MoveBack : StatisticEvent()
    object ShowSelectTimeIntervalDialog : StatisticEvent()
    object CloseDialog : StatisticEvent()
}