package com.sgcdeveloper.moneymanager.domain.use_case

import com.sgcdeveloper.moneymanager.data.db.entry.RateEntry
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.WalletSingleton
import com.sgcdeveloper.moneymanager.util.toSafeDouble
import javax.inject.Inject

class DeleteWallet @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository,
    private val currencyRepository: CurrencyRepository,
    private val getWallets: GetWallets,
    private val insertWallet: InsertWallet
) {
    suspend operator fun invoke(walletId: Long) {
        val transactions = moneyManagerRepository.getWalletTransactions(walletId)
        cancelTransactions(transactions, walletId)
        moneyManagerRepository.removeRecurringTransactions(walletId)
    }

    private suspend fun cancelTransactions(transactions: List<TransactionEntry>, walletId: Long) {
        val wallets = moneyManagerRepository.getAsyncWallets().associate { it.id to it.currency.code }
        val walletsMap = getWallets.getWallets().associateBy { it.walletId }.toMutableMap()
        val rates =
            moneyManagerRepository.getRatesOnce() + RateEntry(0, currencyRepository.getDefaultCurrency(), 1.0)

        transactions.forEach { transaction ->
            val fromWallet = walletsMap[transaction.fromWalletId]!!
            val toWallet = walletsMap[transaction.toWalletId]
            val amount = transaction.value

            when (transaction.transactionType) {
                TransactionType.Expense -> {
                    walletsMap[fromWallet.walletId] = fromWallet.copy(money = (fromWallet.money.toSafeDouble() - amount).toString())
                }
                TransactionType.Income -> {
                    walletsMap[fromWallet.walletId] = fromWallet.copy(money = (fromWallet.money.toSafeDouble() + amount).toString())
                }
                TransactionType.Transfer -> {
                    walletsMap[fromWallet.walletId] = (fromWallet.copy(money = (fromWallet.money.toSafeDouble() + amount * rates.find { it.currency.code == toWallet!!.currency.code }!!.rate / rates.find { it.currency.code == wallets[transaction.toWalletId] }!!.rate).toString()))
                    walletsMap[toWallet!!.walletId] = (toWallet.copy(money = (toWallet.money.toSafeDouble() - amount * rates.find { it.currency.code == toWallet.currency.code }!!.rate / rates.find { it.currency.code == wallets[transaction.fromWalletId] }!!.rate).toString()))
                }
            }
        }

        if (WalletSingleton.wallet.value?.walletId == walletId)
            WalletSingleton.wallet.value = null
        insertWallet.insertWallets(walletsMap.map { it.value })
        moneyManagerRepository.removeWalletTransactions(walletId)
        moneyManagerRepository.removeWallet(walletId)
    }
}