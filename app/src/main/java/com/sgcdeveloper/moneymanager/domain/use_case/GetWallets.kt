package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.sgcdeveloper.moneymanager.domain.model.AddNewWallet
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

class GetWallets @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository,
    private val currencyRepository: CurrencyRepository
) {
    operator fun invoke(): LiveData<List<Wallet>> {
        return Transformations.map(moneyManagerRepository.getWallets()) {
            it.map { wallet ->
                val formatter = NumberFormat.getCurrencyInstance(getLocalFromISO(wallet.currency.code)!!)
                var money = if (wallet.money.rem(1) == 0.0)
                    wallet.money.toInt().toString()
                else
                    wallet.money.toString()
                if (money == "0.0" || money == "0")
                    money = ""
                Wallet(
                    wallet.id,
                    wallet.name,
                    money,
                    formatter.format(wallet.money),
                    wallet.color,
                    getDrawable(wallet.icon),
                    wallet.currency
                )
            }.plus(AddNewWallet(currencyRepository.getDefaultCurrency()))
        }
    }

    private fun getDrawable(name: String): Int {
        val resources: Resources = context.resources
        return resources.getIdentifier(name, "drawable", context.packageName)
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