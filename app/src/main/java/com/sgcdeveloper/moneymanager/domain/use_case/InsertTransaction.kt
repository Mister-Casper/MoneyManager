package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.Date
import com.sgcdeveloper.moneymanager.util.toSafeDouble
import javax.inject.Inject

class InsertTransaction @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository,
    private val insertWallet: InsertWallet,
    private val getWallets: GetWallets
) {
    suspend operator fun invoke(
        transactionId: Long,
        transactionType: TransactionType,
        fromWallet: Wallet,
        toWallet: Wallet? = null,
        description: String,
        amount: String,
        date: Date,
        category: TransactionCategory
    ): Long {
        var toWalletId = 0L
        if (toWallet != null && transactionType == TransactionType.Transfer)
            toWalletId = toWallet.walletId
        if (transactionId != 0L)
            cancelTransaction(transactionId)

        updateWalletMoney(transactionType, amount.toDouble(), fromWallet.walletId, toWallet?.walletId)

        return moneyManagerRepository.insertTransaction(
            TransactionEntry(
                id = transactionId,
                date = date,
                value = amount.toDouble(),
                description = description,
                transactionType = transactionType,
                fromWalletId = fromWallet.walletId,
                toWalletId = toWalletId,
                category = category
            )
        )
    }

    suspend fun deleteTransaction(transactionId: Long){
        cancelTransaction(transactionId)
        moneyManagerRepository.removeTransaction(transactionId)
    }

   private suspend fun cancelTransaction(transactionId: Long) {
        val transaction = moneyManagerRepository.getTransaction(transactionId)
        updateWalletMoney(
            transaction.transactionType, -transaction.value, transaction.fromWalletId, transaction.toWalletId
        )
    }

    private suspend fun updateWalletMoney(
        transactionType: TransactionType,
        amount: Double,
        fromWalletId: Long,
        toWalletId: Long?
    ) {
        val fromWallet = getWallets.getWallet(fromWalletId)
        when (transactionType) {
            TransactionType.Expense -> {
                insertWallet(fromWallet.copy(money = (fromWallet.money.toSafeDouble() - amount).toString()))
            }
            TransactionType.Income -> {
                insertWallet(fromWallet.copy(money = (fromWallet.money.toSafeDouble() + amount).toString()))
            }
            TransactionType.Transfer -> {
                val toWallet = getWallets.getWallet(toWalletId!!)
                insertWallet(fromWallet.copy(money = (fromWallet.money.toSafeDouble() - amount).toString()))
                insertWallet(toWallet.copy(money = (toWallet.money.toSafeDouble() + amount).toString()))
            }
        }
    }
}