package com.sgcdeveloper.moneymanager.domain.use_case

import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.util.SyncHelper
import com.sgcdeveloper.moneymanager.util.WalletSingleton
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
        if(WalletSingleton.wallet.value?.walletId == walletId)
            WalletSingleton.wallet.value = null
        syncHelper.syncServerData()
    }
}