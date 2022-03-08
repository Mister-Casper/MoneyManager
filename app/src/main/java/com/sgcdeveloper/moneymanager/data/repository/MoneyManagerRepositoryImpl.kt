package com.sgcdeveloper.moneymanager.data.repository

import androidx.lifecycle.LiveData
import com.sgcdeveloper.moneymanager.data.db.AppDatabase
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import javax.inject.Inject

class MoneyManagerRepositoryImpl @Inject constructor(private val appDatabase: AppDatabase) : MoneyManagerRepository {
    override fun getWalletsOnce(): List<WalletEntry> {
        return appDatabase.walletDao().getWalletsOnce()
    }

    override fun getWallets(): LiveData<List<WalletEntry>> {
        return appDatabase.walletDao().getWallets()
    }

    override suspend fun getAsyncWallets(): List<WalletEntry> {
        return appDatabase.walletDao().getAsyncWallets()
    }

    override suspend fun getWallet(id: Long): WalletEntry {
        return appDatabase.walletDao().getWallet(id)
    }

    override suspend fun insertWallet(walletEntry: WalletEntry): Long {
        return appDatabase.walletDao().insertWallet(walletEntry)
    }

    override suspend fun removeWallet(id: Long) {
        appDatabase.walletDao().removeWallet(id)
    }

    override suspend fun deleteAllWallets() {
        appDatabase.walletDao().deleteAllWallets()
    }

    override suspend fun insertWallets(wallets: List<WalletEntry>) {
        appDatabase.walletDao().insertWallets(wallets)
    }

    override suspend fun getTransactionsOnce(): List<TransactionEntry> {
        return appDatabase.transactionDao().getTransactionsOnce()
    }

    override fun getTransactions(): LiveData<List<TransactionEntry>> {
        return appDatabase.transactionDao().getTransactions()
    }

    override fun getTransactions(walletId: Long): LiveData<List<TransactionEntry>> {
        return appDatabase.transactionDao().getTransactions(walletId)
    }

    override suspend fun getWalletTransactions(walletId: Long): List<TransactionEntry> {
        return appDatabase.transactionDao().getWalletTransactions(walletId)
    }

    override suspend fun getTransaction(id: Long): TransactionEntry {
        return appDatabase.transactionDao().getTransaction(id)
    }

    override suspend fun insertTransaction(transaction: TransactionEntry): Long {
        return appDatabase.transactionDao().insertTransaction(transaction)
    }

    override suspend fun removeTransaction(id: Long) {
        appDatabase.transactionDao().removeTransaction(id)
    }

    override suspend fun removeWalletTransactions(walletId: Long) {
        appDatabase.transactionDao().removeWalletTransactions(walletId)
    }

    override suspend fun deleteAllTransactions() {
        appDatabase.transactionDao().deleteAllTransactions()
    }

    override suspend fun insertTransactions(transactions: List<TransactionEntry>) {
        appDatabase.transactionDao().insertTransactions(transactions)
    }
}