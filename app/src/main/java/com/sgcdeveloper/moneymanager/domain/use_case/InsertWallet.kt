package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import android.content.res.Resources
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import javax.inject.Inject

class InsertWallet @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository,
) {
    suspend operator fun invoke(wallet: Wallet): Long {
        return moneyManagerRepository.insertWallet(
            WalletEntry(
                id = wallet.walletId,
                name = wallet.name,
                money = wallet.money.toDoubleOrNull() ?: 0.0,
                currency = wallet.currency,
                color = wallet.color,
                icon = getDrawableName(wallet.icon)
            )
        )
    }

    private fun getDrawableName(id: Int): String {
        val resources: Resources = context.resources
        return resources.getResourceName(id)
    }
}