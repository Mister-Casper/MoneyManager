package com.sgcdeveloper.moneymanager.domain.model

import androidx.room.PrimaryKey
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry

open class BaseRecurringTransaction

class RecurringTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionEntry: TransactionEntry,
    val recurringInterval: RecurringInterval,
    val fromWalletId: Long,
    val toWalletId: Long,
    val nextTransactionDate:String,
    val money:String,
    val moneyColor:Int
) : BaseRecurringTransaction()

object AddRecurringTransaction : BaseRecurringTransaction()