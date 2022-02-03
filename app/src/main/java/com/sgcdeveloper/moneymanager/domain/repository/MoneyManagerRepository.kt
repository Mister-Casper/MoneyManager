package com.sgcdeveloper.moneymanager.domain.repository

import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry

interface MoneyManagerRepository {

    suspend fun getWallets(): List<WalletEntry>
    suspend fun insertWallet(walletEntry: WalletEntry): Long
    suspend fun removeWallet(id: Long)

}