package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import android.content.res.Resources
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import javax.inject.Inject

class InsertWallet @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository
) {
    suspend operator fun invoke(wallet: Wallet?): Long {
        if(wallet == null)
            return 0
        val walletId =  moneyManagerRepository.insertWallet(
            WalletEntry(
                id = wallet.walletId,
                isDefault = wallet.isDefault,
                name = wallet.name,
                money = wallet.money.toDoubleOrNull() ?: 0.0,
                currency = wallet.currency,
                color = wallet.color,
                icon = getDrawableName(wallet.icon),
                order = if(wallet.order != -1L) wallet.order else moneyManagerRepository.getLastWalletOrder()?:0 + 1
            )
        )
        return walletId
    }

    suspend fun insertWallets(wallets:List<Wallet>){
         moneyManagerRepository.insertWallets(
             wallets.map { wallet ->
                 WalletEntry(
                     id = wallet.walletId,
                     isDefault = wallet.isDefault,
                     name = wallet.name,
                     money = wallet.money.toDoubleOrNull() ?: 0.0,
                     currency = wallet.currency,
                     color = wallet.color,
                     icon = getDrawableName(wallet.icon),
                     order = wallet.order
                 )
             }
        )
    }

    private fun getDrawableName(id: Int): String {
        val resources: Resources = context.resources
        return resources.getResourceName(id)
    }
}