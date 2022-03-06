package com.sgcdeveloper.moneymanager.data.prefa

import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import java.time.DayOfWeek
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSettings @Inject constructor() {

    val defaultScreen = BottomMoneyManagerNavigationScreens.Transactions.route
    val defaultTransactionType = TransactionType.Expense
    val loginStatus: LoginStatus = LoginStatus.Registering
    var firstDayOfWeek = DayOfWeek.MONDAY.value

    init {
        if (Locale.getDefault().country == "US") {
            firstDayOfWeek = DayOfWeek.SUNDAY.value
        }
    }
}