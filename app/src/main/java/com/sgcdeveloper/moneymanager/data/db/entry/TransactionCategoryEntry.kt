package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TransactionCategoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val color: String = "wallet_color_1",
    val icon: String = "food_icon",
    val description: String,
    val isDefault: Int = 0,
    val isExpense: Int = 0,
    val order:Int = 0
) {
    fun toObject(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "color" to color,
            "icon" to icon,
            "description" to description,
            "isDefault" to isDefault,
            "isExpense" to isExpense,
            "order" to order
        )
    }

    companion object {
        fun getTransactionCategoryEntry(data: MutableMap<String, Any>): TransactionCategoryEntry {
            return TransactionCategoryEntry(
                data["id"] as Long,
                data["color"] as String,
                data["icon"] as String,
                data["description"] as String,
                data["isDefault"] as Int,
                data["isExpense"] as Int,
                data["order"] as Int
            )
        }
    }
}