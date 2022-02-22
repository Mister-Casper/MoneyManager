package com.sgcdeveloper.moneymanager.domain.use_case

import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.util.SyncHelper
import javax.inject.Inject

class DeleteWallet @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository,
    private val insertTransaction:InsertTransaction,
    private val syncHelper: SyncHelper
) {
    suspend operator fun invoke(walletId: Long) {
        val transactions = moneyManagerRepository.getWalletTransactions(walletId)
        transactions.forEach{transaction ->  insertTransaction.cancelTransaction(transaction.id)}
        moneyManagerRepository.removeWalletTransactions(walletId)
        moneyManagerRepository.removeWallet(walletId)
        syncHelper.syncServerData()
    }
}