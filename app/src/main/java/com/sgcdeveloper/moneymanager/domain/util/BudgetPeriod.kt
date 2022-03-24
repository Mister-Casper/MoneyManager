package com.sgcdeveloper.moneymanager.domain.util

import androidx.annotation.StringRes
import com.sgcdeveloper.moneymanager.R

enum class BudgetPeriod (@StringRes val periodNameRes:Int,val fullNameRes:Int){
    Daily(R.string.daily,R.string.daily_full),
    Weekly(R.string.weekly,R.string.weekly_full),
    Monthly(R.string.monthly,R.string.monthly_full),
    Quarterly(R.string.quarterly,R.string.quarterly_full),
    Yearly(R.string.yearly,R.string.yearly_full),
}