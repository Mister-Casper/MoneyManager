package com.sgcdeveloper.moneymanager.domain.use_case

import com.sgcdeveloper.moneymanager.domain.model.AllWallets
import com.sgcdeveloper.moneymanager.domain.model.Transaction
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository,
    private val currencyRepository: CurrencyRepository
) {

    suspend operator fun invoke(wallet: Wallet): List<Transaction> = CoroutineScope(Dispatchers.IO).async {
        return@async if (wallet is AllWallets || wallet.walletId == 0L) {
            val wallets = moneyManagerRepository.getAsyncWallets().associate { it.id to it.currency.code }
            val rates = moneyManagerRepository.getRatesOnce()
            val defaultCurrency = currencyRepository.getDefaultCurrency()
            val transactions = moneyManagerRepository.getTransactionsOnce().toMutableList()
            transactions.forEachIndexed { item, transaction ->
                val transactionCode = wallets[transaction.fromWalletId]
                if (transactionCode != defaultCurrency.code) {
                    transactions[item] =
                        transaction.copy(value = transaction.value / rates.find { it.currency.code == transactionCode }!!.rate)
                }
            }
            transactions.map { transaction ->
                val transactionCode = wallets[transaction.fromWalletId]
                if (transactionCode != defaultCurrency.code) {
                    Transaction(
                        transaction.id,
                        transaction.date,
                        transaction.value,
                        transaction.value * rates.find { it.currency.code == wallets[transaction.fromWalletId] }!!.rate,
                        transaction.description,
                        transaction.transactionType,
                        transaction.fromWalletId,
                        transaction.toWalletId,
                        transaction.category
                    )
                }else{
                    Transaction(
                        transaction.id,
                        transaction.date,
                        transaction.value,
                        transaction.value,
                        transaction.description,
                        transaction.transactionType,
                        transaction.fromWalletId,
                        transaction.toWalletId,
                        transaction.category
                    )
                }
            }
        } else {
            moneyManagerRepository.getWalletTransactions(wallet.walletId).map {
                Transaction(
                    it.id,
                    it.date,
                    it.value,
                    it.value,
                    it.description,
                    it.transactionType,
                    it.fromWalletId,
                    it.toWalletId,
                    it.category
                )
            }
        }.sortedByDescending { it.date.epochMillis }
    }.await()

}