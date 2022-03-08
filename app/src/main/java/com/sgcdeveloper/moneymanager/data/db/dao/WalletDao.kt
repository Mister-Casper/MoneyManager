package com.sgcdeveloper.moneymanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry

@Dao
interface WalletDao {

    @Query("SELECT * FROM WalletEntry")
    fun getWalletsOnce(): List<WalletEntry>

    @Query("SELECT * FROM WalletEntry")
    fun getWallets(): LiveData<List<WalletEntry>>

    @Query("SELECT * FROM WalletEntry")
    suspend fun getAsyncWallets(): List<WalletEntry>

    @Query("SELECT * FROM WalletEntry WHERE id == :id")
    suspend fun getWallet(id:Long): WalletEntry

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(walletEntry: WalletEntry):Long

    @Query("DELETE FROM WalletEntry WHERE id = :id")
    suspend fun removeWallet(id: Long)

    @Query("DELETE FROM WalletEntry")
    suspend fun deleteAllWallets()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallets(wallets: List<WalletEntry>)
}
