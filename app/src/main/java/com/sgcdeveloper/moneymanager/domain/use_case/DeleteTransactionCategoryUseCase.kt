package com.sgcdeveloper.moneymanager.domain.use_case

import com.sgcdeveloper.moneymanager.data.db.TransactionCategoriesDatabase
import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.toSafeDouble
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteTransactionCategoryUseCase @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository,
    private val getWallets: GetWallets,
    private val insertWallet: InsertWallet,
    private val transactionCategoriesDatabase: TransactionCategoriesDatabase
) {
    suspend operator fun invoke(transactionCategoryId: Long) =
        withContext(CoroutineScope(Dispatchers.Main).coroutineContext) {
            val transactions =
                moneyManagerRepository.getTransactionsOnce().filter { it.category.id == transactionCategoryId }
            cancelTransactions(transactions)
            val budgets = mutableListOf<BudgetEntry>()
            moneyManagerRepository.getAsyncWBudgets().map { budget ->
                val categories = budget.categories.map { it.id }
                if (transactionCategoryId in categories) {
                    if (categories.size > 1) {
                        budgets.add(budget.copy(categories = budget.categories.filter { it.id != transactionCategoryId }))
                    }
                }
            }
            moneyManagerRepository.deleteAllBudgets()
            moneyManagerRepository.insertBudgets(budgets)
            moneyManagerRepository.removeRecurringTransactionsWithCategoryId(transactionCategoryId)
            transactionCategoriesDatabase.transactionCategoryDao()
                .removeTransactionCategoryEntry(transactionCategoryId)
        }

    private suspend fun cancelTransactions(transactions: List<TransactionEntry>) {
        val walletsMap = getWallets.getWallets().associateBy { it.walletId }.toMutableMap()

        transactions.forEach { transaction ->
            val fromWallet = walletsMap[transaction.fromWalletId]!!
            val amount = transaction.value

            when (transaction.transactionType) {
                TransactionType.Expense -> {
                    walletsMap[fromWallet.walletId] =
                        fromWallet.copy(money = (fromWallet.money.toSafeDouble() + amount).toString())
                }
                TransactionType.Income -> {
                    walletsMap[fromWallet.walletId] =
                        fromWallet.copy(money = (fromWallet.money.toSafeDouble() - amount).toString())
                }
            }
        }

        insertWallet.insertWallets(walletsMap.map { it.value })
        moneyManagerRepository.removeTransactions(transactions.map { it.id.toInt() })
    }
}