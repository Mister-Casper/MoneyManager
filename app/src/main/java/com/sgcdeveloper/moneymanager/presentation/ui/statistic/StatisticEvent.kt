package com.sgcdeveloper.moneymanager.presentation.ui.statistic

import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController

sealed class StatisticEvent {
    class ChangeTimeInterval(val timeIntervalController: TimeIntervalController) : StatisticEvent()
    class SetWallet(val wallet: Wallet) : StatisticEvent()
    class ChangeWalletById(val walletId: Long) : StatisticEvent()

    object ShowWalletPickerDialog : StatisticEvent()
    object MoveNext : StatisticEvent()
    object MoveBack : StatisticEvent()
    object ShowSelectTimeIntervalDialog : StatisticEvent()
    object CloseDialog : StatisticEvent()
}