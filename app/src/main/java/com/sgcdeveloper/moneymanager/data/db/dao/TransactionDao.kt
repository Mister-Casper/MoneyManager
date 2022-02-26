package com.sgcdeveloper.moneymanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry

@Dao
interface TransactionDao {

    @Query("SELECT * FROM TransactionEntry")
    suspend fun getTransactionsOnce(): List<TransactionEntry>

    @Query("SELECT * FROM TransactionEntry")
    fun getTransactions(): LiveData<List<TransactionEntry>>

    @Query("SELECT * FROM TransactionEntry WHERE fromWalletId == :walletId OR toWalletId == :walletId")
    fun getTransactions(walletId:Long): LiveData<List<TransactionEntry>>

    @Query("SELECT * FROM TransactionEntry WHERE fromWalletId == :walletId OR toWalletId == :walletId")
    suspend fun getWalletTransactions(walletId:Long): List<TransactionEntry>

    @Query("SELECT * FROM TransactionEntry WHERE id == :id")
    suspend fun getTransaction(id: Long):TransactionEntry

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntry):Long

    @Query("DELETE FROM TransactionEntry WHERE id = :id")
    suspend fun removeTransaction(id: Long)

    @Query("DELETE FROM TransactionEntry WHERE fromWalletId == :walletId OR toWalletId == :walletId")
    suspend fun removeWalletTransactions(walletId:Long)

    @Query("DELETE FROM TransactionEntry")
    suspend fun deleteAllTransactions()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntry>)
}
