package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.data.util.RecurringIntervalSaver
import com.sgcdeveloper.moneymanager.domain.model.RecurringInterval

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
            "transactionEntry" to Gson().toJson(transactionEntry),
            "recurringInterval" to Gson().toJson(RecurringIntervalSaver(recurringInterval.recurring, Gson().toJson(recurringInterval))),
            "fromWalletId" to fromWalletId,
            "toWalletId" to toWalletId
        )
    }

    companion object {
        fun getRecurringTransactionEntry(data: MutableMap<String, Any>): RecurringTransactionEntry {
            return RecurringTransactionEntry(
                data["id"] as Long,
                Gson().fromJson(data["transactionEntry"] as String,TransactionEntry::class.java),
                Gson().fromJson(data["recurringInterval"] as String,RecurringIntervalSaver::class.java).toRecurringInterval(),
                data["fromWalletId"] as Long,
                data["toWalletId"] as Long
            )
        }
    }
}