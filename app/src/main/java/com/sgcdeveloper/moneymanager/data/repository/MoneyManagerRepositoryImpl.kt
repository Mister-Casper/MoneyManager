package com.sgcdeveloper.moneymanager.data.repository

import androidx.lifecycle.LiveData
import com.sgcdeveloper.moneymanager.data.db.AppDatabase
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import javax.inject.Inject

class MoneyManagerRepositoryImpl @Inject constructor(private val appDatabase: AppDatabase) : MoneyManagerRepository {
    override fun getWallets(): LiveData<List<WalletEntry>> {
        return appDatabase.walletDao().getWallets()
    }

    override suspend fun insertWallet(walletEntry: WalletEntry): Long {
        return appDatabase.walletDao().insertWallet(walletEntry)
    }

    override suspend fun removeWallet(id: Long) {
        appDatabase.walletDao().removeWallet(id)
    }
}