package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson


@Entity
data class RecurringTransactionEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionEntry: TransactionEntry,

    ) {
    fun toObject(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "transactionEntry" to Gson().toJson(transactionEntry)
        )
    }

    companion object {
        fun getRecurringTransactionEntry(data: MutableMap<String, Any>): RecurringTransactionEntry {
            return RecurringTransactionEntry(
                data["id"] as Long,
                Gson().fromJson(data["transactionEntry"] as String,TransactionEntry::class.java)
            )
        }
    }
}