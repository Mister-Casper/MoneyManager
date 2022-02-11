package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.util.Date
import javax.inject.Inject

class InsertTransaction @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository
) {
    suspend operator fun invoke(
        transactionType: TransactionType,
        fromWallet: Wallet,
        toWallet: Wallet ?= null,
        description: String,
        amount: String,
        date: Date,
        category: TransactionCategory
    ): Long {
        var toWalletId = 0L
        if(toWallet != null)
            toWalletId = toWallet.walletId
        return moneyManagerRepository.insertTransaction(
            TransactionEntry(
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


}