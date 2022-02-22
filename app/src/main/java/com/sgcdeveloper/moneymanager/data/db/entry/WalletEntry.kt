package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
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
){
    fun toObject(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "isDefault" to isDefault,
            "name" to name,
            "money" to money,
            "currency" to Gson().toJson(currency),
            "color" to color,
            "icon" to icon
        )
    }

    companion object{
        fun getWalletByHashMap(data: MutableMap<String, Any>): WalletEntry {
            return WalletEntry(
                data["id"] as Long,
                data["isDefault"] as Boolean,
                data["name"] as String,
                data["money"] as Double,
                Gson().fromJson(data["currency"] as String,Currency::class.java),
                (data["color"] as Long).toInt(),
                data["icon"] as String
            )
        }
    }
}