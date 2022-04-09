package com.sgcdeveloper.moneymanager.domain.model

import com.github.mikephil.charting.data.PieEntry

data class CategoryStatistic(
    var pieEntry: PieEntry = PieEntry(0f),
    val sum:Double,
    val category: String,
    val categoryEntry: TransactionCategory,
    val color: Int,
    val moneyColor: Int,
    val icon: Int,
    val money: String,
    val count:String,
    var percent: String = "0.0"
)