package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.domain.model.Currency

@Entity
class RateEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val currency: Currency,
    val rate:Double
) {
    fun toObject(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "currency" to Gson().toJson(currency),
            "rate" to rate
        )
    }

    companion object {
        fun getRateByHashMap(data: MutableMap<String, Any>): RateEntry {
            val order = if(data["order"] == null){
                data["id"]
            }else{
                data["order"]
            }
            return RateEntry(
                data["id"] as Long,
                Gson().fromJson(data["currency"] as String, Currency::class.java),
                data["rate"] as Double
            )
        }
    }
}