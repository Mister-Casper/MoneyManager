package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sgcdeveloper.moneymanager.data.util.RecurringIntervalSaver
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval
import com.sgcdeveloper.moneymanager.util.gson

@Entity
data class RecurringTransactionEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionEntry: TransactionEntry,
    val recurringInterval: RecurringInterval,
    val fromWalletId:Long,
    val toWalletId:Long
    ) {
    fun toObject(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "transactionEntry" to gson.toJson(transactionEntry),
            "recurringInterval" to gson.toJson(RecurringIntervalSaver(recurringInterval.recurring, gson.toJson(recurringInterval))),
            "fromWalletId" to fromWalletId,
            "toWalletId" to toWalletId
        )
    }

    companion object {
        fun getRecurringTransactionEntry(data: MutableMap<String, Any>): RecurringTransactionEntry {
            return RecurringTransactionEntry(
                data["id"] as Long,
                gson.fromJson(data["transactionEntry"] as String,TransactionEntry::class.java),
                gson.fromJson(data["recurringInterval"] as String,RecurringIntervalSaver::class.java).toRecurringInterval(),
                data["fromWalletId"] as Long,
                data["toWalletId"] as Long
            )
        }
    }
}