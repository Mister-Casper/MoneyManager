package com.sgcdeveloper.moneymanager.domain.model

import com.github.mikephil.charting.data.Entry

class BudgetGraphEntry(x: Float, y: Float, val money: String, val date: String) : Entry(x, y)