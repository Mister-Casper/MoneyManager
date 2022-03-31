package com.sgcdeveloper.moneymanager.data.repository

import androidx.lifecycle.LiveData
import com.sgcdeveloper.moneymanager.data.db.AppDatabase
import com.sgcdeveloper.moneymanager.data.db.entry.*
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

    override suspend fun getLastWalletOrder(): Long {
        return appDatabase.walletDao().getLastWalletOrder()
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

    override suspend fun getRatesOnce(): List<RateEntry> {
        return appDatabase.rateDao().getRatesOnce()
    }

    override fun getRates(): LiveData<List<RateEntry>> {
        return appDatabase.rateDao().getRates()
    }

    override suspend fun insertRates(rateEntries: List<RateEntry>) {
        appDatabase.rateDao().insertRates(rateEntries)
    }

    override suspend fun insertRate(rateEntry: RateEntry): Long {
        return appDatabase.rateDao().insertRate(rateEntry)
    }

    override suspend fun deleteAllRates() {
        appDatabase.rateDao().deleteAllRates()
    }

    override fun getBudgetsOnce(): List<BudgetEntry> {
        return appDatabase.budgetDao().getBudgetsOnce()
    }

    override fun getBudgets(): LiveData<List<BudgetEntry>> {
        return appDatabase.budgetDao().getBudgets()
    }

    override suspend fun getAsyncWBudgets(): List<BudgetEntry> {
        return appDatabase.budgetDao().getAsyncWBudgets()
    }

    override suspend fun getBudget(id: Long): BudgetEntry {
        return appDatabase.budgetDao().getBudget(id)
    }

    override suspend fun insertBudget(budgetEntry: BudgetEntry): Long {
        return appDatabase.budgetDao().insertBudget(budgetEntry)
    }

    override suspend fun removeBudget(id: Long) {
        return appDatabase.budgetDao().removeBudget(id)
    }

    override suspend fun deleteAllBudgets() {
        return appDatabase.budgetDao().deleteAllBudgets()
    }

    override suspend fun insertBudgets(budgetEntries: List<BudgetEntry>) {
        return appDatabase.budgetDao().insertBudgets(budgetEntries)
    }

    override suspend fun getRecurringTransactionsOnce(): List<RecurringTransactionEntry> {
        return appDatabase.recurringTransactionDao().getRecurringTransactionsOnce()
    }

    override fun getRecurringTransactions(): LiveData<List<RecurringTransactionEntry>> {
        return appDatabase.recurringTransactionDao().getRecurringTransactions()
    }

    override suspend fun getRecurringTransaction(id: Long): RecurringTransactionEntry {
        return appDatabase.recurringTransactionDao().getRecurringTransaction(id)
    }

    override suspend fun insertRecurringTransaction(transaction: RecurringTransactionEntry): Long {
        return appDatabase.recurringTransactionDao().insertRecurringTransaction(transaction)
    }

    override suspend fun removeRecurringTransaction(id: Long) {
        appDatabase.recurringTransactionDao().removeRecurringTransaction(id)
    }

    override suspend fun deleteAllRecurringTransactions() {
        appDatabase.recurringTransactionDao().deleteAllRecurringTransactions()
    }

    override suspend fun insertRecurringTransactions(transactions: List<RecurringTransactionEntry>) {
        appDatabase.recurringTransactionDao().insertRecurringTransactions(transactions)
    }

    override suspend fun removeRecurringTransactions(walletId: Long) {
        appDatabase.recurringTransactionDao().removeRecurringTransactions(walletId)
    }
}