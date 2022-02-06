package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import android.content.res.Resources
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

class InsertWallet @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository,
) {
    suspend operator fun invoke(wallet: Wallet): Long {
        return moneyManagerRepository.insertWallet(
            WalletEntry(
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

    companion object {
        fun getLocalFromISO(iso4217code: String): Locale? {
            var toReturn: Locale? = null
            for (locale in NumberFormat.getAvailableLocales()) {
                val code = NumberFormat.getCurrencyInstance(locale).currency!!.currencyCode
                if (iso4217code == code) {
                    toReturn = locale
                    break
                }
            }
            return toReturn
        }
    }
}