package com.sgcdeveloper.moneymanager.domain.model

import com.github.mikephil.charting.data.PieEntry

data class CategoryStatistic(
    var pieEntry: PieEntry = PieEntry(0f),
    val sum:Double,
    val category: String,
    val color: Int,
    val money: String,
    var percent: Double = 0.0
)