package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_1

@Entity
class WalletEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val isDefault:Boolean,
    val name: String,
    val money: Double,
    val currency: Currency,
    val color:Int = wallet_color_1.toArgb(),
    val icon:String = "wallet_icon_1"
)