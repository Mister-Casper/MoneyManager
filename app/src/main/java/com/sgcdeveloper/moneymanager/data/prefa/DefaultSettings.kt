package com.sgcdeveloper.moneymanager.data.prefa

import java.time.DayOfWeek
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSettings @Inject constructor() {

    val loginStatus: LoginStatus = LoginStatus.Registering
    var firstDayOfWeek = DayOfWeek.MONDAY.value

    init {
        if (Locale.getDefault().country == "US") {
            firstDayOfWeek = DayOfWeek.SUNDAY.value
        }
    }
}