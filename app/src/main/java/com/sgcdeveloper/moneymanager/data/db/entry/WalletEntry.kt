package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sgcdeveloper.moneymanager.domain.model.Currency

@Entity
class WalletEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val money: Double,
    val currency: Currency
)