package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.Date

@Entity
class TransactionEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date:Date,
    val value: Double,
    val description: String,
    val transactionType: TransactionType,
    val fromWalletId: Long,
    val toWalletId: Long = 0
)