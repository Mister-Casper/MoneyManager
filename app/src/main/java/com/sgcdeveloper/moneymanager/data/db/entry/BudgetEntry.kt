package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_1
import com.sgcdeveloper.moneymanager.util.Date
import java.lang.reflect.Type

@Entity
class BudgetEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val budgetName: String,
    val amount: Double,
    val categories: List<TransactionCategory.ExpenseCategory>,
    val color: Int = wallet_color_1.toArgb(),
    val date: Date,
    val period: BudgetPeriod
) {
    fun toObject(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "budgetName" to budgetName,
            "amount" to amount,
            "categories" to Gson().toJson(categories),
            "color" to color,
            "date" to date.epochMillis,
            "period" to period.ordinal
        )
    }

    companion object {
        fun getBudgetByHashMap(data: MutableMap<String, Any>): BudgetEntry {
            val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.type

            return BudgetEntry(
                data["id"] as Long,
                data["budgetName"] as String,
                data["amount"] as Double,
                Gson().fromJson(data["categories"] as String, listType),
                (data["color"] as Long).toInt(),
                Date(data["date"] as Long),
                BudgetPeriod.values().find { it.ordinal == data["period"] }!!
            )
        }
    }
}