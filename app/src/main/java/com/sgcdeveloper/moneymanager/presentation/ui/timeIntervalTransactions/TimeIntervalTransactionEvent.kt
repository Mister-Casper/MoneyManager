package com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions

import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController

sealed class TimeIntervalTransactionEvent {
    class SetDefaultWallet(val wallet: Wallet) : TimeIntervalTransactionEvent()
    class ChangeTimeInterval(val timeIntervalController: TimeIntervalController) : TimeIntervalTransactionEvent()

    object MoveNext : TimeIntervalTransactionEvent()
    object MoveBack : TimeIntervalTransactionEvent()
    object ShowSelectTimeIntervalDialog : TimeIntervalTransactionEvent()
    object CloseDialog : TimeIntervalTransactionEvent()
}