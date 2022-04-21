package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sgcdeveloper.moneymanager.data.db.util.ListConverter
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_1
import com.sgcdeveloper.moneymanager.util.Date

@Entity
data class BudgetEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val budgetName: String,
    val amount: Double,
    var categories: List<TransactionCategory>,
    val color: Int = wallet_color_1.toArgb(),
    val date: Date,
    val period: BudgetPeriod
) {
    fun toObject(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "budgetName" to budgetName,
            "amount" to amount,
            "categories" to listConverter.fromArrayList(categories),
            "color" to color,
            "date" to date.epochMillis,
            "period" to period.ordinal
        )
    }

    companion object {
        lateinit var listConverter:ListConverter

        fun getBudgetByHashMap(data: MutableMap<String, Any>): BudgetEntry {
            return BudgetEntry(
                data["id"] as Long,
                data["budgetName"] as String,
                data["amount"] as Double,
                listConverter.fromString(data["categories"] as String),
                (data["color"] as Long).toInt(),
                Date(data["date"] as Long),
                BudgetPeriod.values().find { it.ordinal == (data["period"] as Long).toInt() }!!
            )
        }
    }
}