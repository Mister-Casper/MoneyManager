package com.sgcdeveloper.moneymanager.presentation.ui.weeklyStatisticScreen

import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.util.TransactionType

sealed class WeeklyStatisticScreenEvent {
    class Init(val wallet: Wallet,val transactionType: TransactionType) : WeeklyStatisticScreenEvent()

    object MoveBack : WeeklyStatisticScreenEvent()
    object MoveNext : WeeklyStatisticScreenEvent()
}