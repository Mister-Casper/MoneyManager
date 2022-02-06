package com.sgcdeveloper.moneymanager.domain.repository

import androidx.lifecycle.LiveData
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry

interface MoneyManagerRepository {

    fun getWallets(): LiveData<List<WalletEntry>>
    suspend fun insertWallet(walletEntry: WalletEntry): Long
    suspend fun removeWallet(id: Long)

}