package com.sgcdeveloper.moneymanager.data.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.util.gson

@Entity
class RateEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val currency: Currency,
    val rate:Double
) {
    fun toObject(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "currency" to gson.toJson(currency),
            "rate" to rate
        )
    }

    companion object {
        fun getRateByHashMap(data: MutableMap<String, Any>): RateEntry {
            return RateEntry(
                data["id"] as Long,
                gson.fromJson(data["currency"] as String, Currency::class.java),
                data["rate"] as Double
            )
        }
    }
}