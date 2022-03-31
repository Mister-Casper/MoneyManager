package com.sgcdeveloper.moneymanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sgcdeveloper.moneymanager.data.db.entry.RecurringTransactionEntry

@Dao
interface RecurringTransactionDao {

    @Query("SELECT * FROM RecurringTransactionEntry")
    suspend fun getRecurringTransactionsOnce(): List<RecurringTransactionEntry>

    @Query("SELECT * FROM RecurringTransactionEntry")
    fun getRecurringTransactions(): LiveData<List<RecurringTransactionEntry>>

    @Query("SELECT * FROM RecurringTransactionEntry WHERE id == :id")
    suspend fun getRecurringTransaction(id: Long):RecurringTransactionEntry

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringTransaction(transaction: RecurringTransactionEntry):Long

    @Query("DELETE FROM RecurringTransactionEntry WHERE id = :id")
    suspend fun removeRecurringTransaction(id: Long)

    @Query("DELETE FROM RecurringTransactionEntry")
    suspend fun deleteAllRecurringTransactions()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringTransactions(transactions: List<RecurringTransactionEntry>)

    @Query("DELETE FROM RecurringTransactionEntry WHERE fromWalletId == :walletId OR toWalletId == :walletId")
    suspend fun removeRecurringTransactions(walletId:Long)
}
