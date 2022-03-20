package com.sgcdeveloper.moneymanager.domain.util

import androidx.annotation.StringRes
import com.sgcdeveloper.moneymanager.R

enum class BudgetPeriod (@StringRes val periodNameRes:Int){
    Daily(R.string.daily),
    Weekly(R.string.weekly),
    Monthly(R.string.monthly),
    Quarterly(R.string.quarterly),
    Yearly(R.string.yearly)
}