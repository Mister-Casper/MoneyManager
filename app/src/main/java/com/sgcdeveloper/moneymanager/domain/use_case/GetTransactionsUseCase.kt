package com.sgcdeveloper.moneymanager.domain.use_case

import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.AllWallets
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

    suspend operator fun invoke(wallet: Wallet): List<TransactionEntry> = CoroutineScope(Dispatchers.IO).async {
        return@async if (wallet is AllWallets || wallet.walletId == 0L) {
            val wallets = moneyManagerRepository.getAsyncWallets().associate { it.id to it.currency.code }
            val rates = moneyManagerRepository.getRatesOnce()
            val defaultCurrency = currencyRepository.getDefaultCurrency()
            val transactions = moneyManagerRepository.getTransactionsOnce().toMutableList()
            transactions.forEachIndexed{ item,transaction ->
                val transactionCode = wallets[transaction.fromWalletId]
                if (transactionCode != defaultCurrency.code) {
                    transactions[item] = transaction.copy(value = transaction.value / rates.find { it.currency.code == transactionCode }!!.rate)
                }
            }
            transactions
        } else {
            moneyManagerRepository.getWalletTransactions(wallet.walletId)
        }.sortedByDescending { it.date.epochMillis }
    }.await()

}