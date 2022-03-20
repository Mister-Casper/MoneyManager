package com.sgcdeveloper.moneymanager.domain.use_case

import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import javax.inject.Inject

class DeleteWallet @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository,
    private val insertTransaction: InsertTransaction
) {
    suspend operator fun invoke(walletId: Long) {
        val transactions = moneyManagerRepository.getWalletTransactions(walletId)
        insertTransaction.cancelTransactions(transactions, walletId)
    }
}