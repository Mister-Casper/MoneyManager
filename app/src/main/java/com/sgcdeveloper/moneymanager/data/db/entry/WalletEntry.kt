package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.presentation.theme.blue

@Entity
class WalletEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val money: Double,
    val currency: Currency,
    val color:Int = blue.toArgb(),
    val icon:String = "wallet_icon_1"
)