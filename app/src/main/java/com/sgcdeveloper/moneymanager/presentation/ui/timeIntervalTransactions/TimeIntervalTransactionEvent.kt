package com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions

import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory

sealed class TimeIntervalTransactionEvent {
    class SetDefaultWallet(val wallet: Wallet) : TimeIntervalTransactionEvent()
    class SetDefaultWalletId(val walletId: Long) : TimeIntervalTransactionEvent()
    class ChangeTimeInterval(val timeIntervalController: TimeIntervalController) : TimeIntervalTransactionEvent()
    class ChangeTransactionCategoryFilter(val category:TransactionCategory) : TimeIntervalTransactionEvent()

    object MoveNext : TimeIntervalTransactionEvent()
    object MoveBack : TimeIntervalTransactionEvent()
    object ShowSelectTimeIntervalDialog : TimeIntervalTransactionEvent()
    object CloseDialog : TimeIntervalTransactionEvent()
}