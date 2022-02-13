package com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions

import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeInterval

sealed class TimeIntervalTransactionEvent {
    class SetDefaultWallet(val wallet: Wallet) : TimeIntervalTransactionEvent()
    class ChangeTimeInterval(val timeInterval: TimeInterval) : TimeIntervalTransactionEvent()

    object MoveNext : TimeIntervalTransactionEvent()
    object MoveBack : TimeIntervalTransactionEvent()
    object ShowSelectTimeIntervalDialog : TimeIntervalTransactionEvent()
    object CloseDialog : TimeIntervalTransactionEvent()
}