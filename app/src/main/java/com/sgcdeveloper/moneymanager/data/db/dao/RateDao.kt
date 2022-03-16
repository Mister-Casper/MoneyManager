package com.sgcdeveloper.moneymanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sgcdeveloper.moneymanager.data.db.entry.RateEntry

@Dao
interface RateDao {
    @Query("SELECT * FROM RateEntry")
    suspend fun getRatesOnce(): List<RateEntry>

    @Query("SELECT * FROM RateEntry")
    fun getRates(): LiveData<List<RateEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rateEntries: List<RateEntry>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(rateEntry: RateEntry):Long

    @Query("DELETE FROM RateEntry")
    suspend fun deleteAllRates()
}
