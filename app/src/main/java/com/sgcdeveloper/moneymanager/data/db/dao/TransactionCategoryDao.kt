package com.sgcdeveloper.moneymanager.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionCategoryEntry

@Dao
interface TransactionCategoryDao {

    @Query("SELECT * FROM TransactionCategoryEntry")
    suspend fun getTransactionCategories(): List<TransactionCategoryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionCategory(transaction: TransactionCategoryEntry):Long

    @Query("DELETE FROM TransactionCategoryEntry WHERE id = :id")
    suspend fun removeTransactionCategoryEntry(id: Long)

    @Query("DELETE FROM TransactionCategoryEntry")
    suspend fun deleteAllTransactionCategoryEntry()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionCategoryEntries(transactions: List<TransactionCategoryEntry>)
}
