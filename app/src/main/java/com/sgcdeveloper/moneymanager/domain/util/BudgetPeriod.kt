package com.sgcdeveloper.moneymanager.domain.util

import androidx.annotation.StringRes
import com.sgcdeveloper.moneymanager.R

enum class BudgetPeriod(@StringRes val periodNameRes: Int, val fullNameRes: Int, val mediumNameRes: Int) {
    Daily(R.string.daily, R.string.daily_full, R.string.daily_medium),
    Weekly(R.string.weekly, R.string.weekly_full, R.string.weekly_medium),
    Monthly(R.string.monthly, R.string.monthly_full, R.string.monthly_medium),
    Quarterly(R.string.quarterly, R.string.quarterly_full, R.string.quarterly_medium),
    Yearly(R.string.yearly, R.string.yearly_full, R.string.yearly_medium),
}