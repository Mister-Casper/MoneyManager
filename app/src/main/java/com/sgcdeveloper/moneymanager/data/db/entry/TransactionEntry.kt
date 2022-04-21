package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.Date

@Entity
data class TransactionEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Date,
    val value: Double,
    val description: String,
    val transactionType: TransactionType,
    val fromWalletId: Long,
    val toWalletId: Long = 0,
    val category: TransactionCategory,
    val fromTransferValue: Double = 0.0,
    val toTransferValue: Double = 0.0,
) {
    fun toObject(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "date" to date.epochMillis,
            "value" to value,
            "description" to description,
            "transactionType" to transactionType.ordinal,
            "fromWalletId" to fromWalletId,
            "toWalletId" to toWalletId,
            "category" to category.id,
            "fromTransferValue" to fromTransferValue,
            "toTransferValue" to toTransferValue
        )
    }

    companion object {
        fun getTaskByHashMap(
            categories: Map<Int, TransactionCategory>,
            data: MutableMap<String, Any>
        ): TransactionEntry {
            val fromTransferValue =  (data["fromTransferValue"]?:"0.0") as Double
            val toTransferValue =  (data["toTransferValue"]?:"0.0") as Double

            return TransactionEntry(
                data["id"] as Long,
                Date(data["date"] as Long),
                data["value"] as Double,
                data["description"] as String,
                TransactionType.getByOrdinal((data["transactionType"] as Long).toInt()),
                data["fromWalletId"] as Long,
                data["toWalletId"] as Long,
                categories[(data["category"] as Long).toInt()]
                    ?: throw Exception("Cant find transaction category with id = " + data["category"] as Long),
                fromTransferValue,
                toTransferValue
            )
        }
    }
}