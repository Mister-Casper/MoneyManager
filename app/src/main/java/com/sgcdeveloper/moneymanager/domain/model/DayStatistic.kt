package com.sgcdeveloper.moneymanager.domain.model

import com.github.mikephil.charting.data.BarEntry

data class DayStatistic(
    val dayName: String,
    val dateName: String,
    val sum: String,
    val colorSum: Int,
    val countTransactions: String,
    val entry: BarEntry
)