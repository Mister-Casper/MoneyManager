package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
import com.sgcdeveloper.moneymanager.domain.model.AddNewWallet
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.util.toMoneyString
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
            transformWallets(it)
        }
    }

    fun getUIWallets(): LiveData<List<Wallet>> {
        return Transformations.map(moneyManagerRepository.getWallets()) {
            transformWallets(it).plus(AddNewWallet(currencyRepository.getDefaultCurrency()))
        }
    }

    suspend fun getWallet(id: Long): Wallet {
        return transformWallet(moneyManagerRepository.getWallet(id))
    }

    private fun transformWallets(wallets: List<WalletEntry>): List<Wallet> {
        return wallets.map { wallet -> transformWallet(wallet) }
    }

    private fun transformWallet(wallet: WalletEntry): Wallet {
        val formatter = NumberFormat.getCurrencyInstance(getLocalFromISO(wallet.currency.code)!!)
        var money = wallet.money.toMoneyString()
        if (money == "0.0" || money == "0")
            money = ""
        return Wallet(
            wallet.id,
            wallet.isDefault,
            wallet.name,
            money,
            formatter.format(wallet.money),
            wallet.color,
            getDrawable(wallet.icon),
            wallet.currency
        )
    }

    private fun getDrawable(name: String): Int {
        val resources: Resources = context.resources
        return resources.getIdentifier(name, "drawable", context.packageName)
    }

    companion object {
        private val locales = HashMap<String,Locale>()
        private val formatters = HashMap<Locale,NumberFormat>()

        fun getLocalFromISO(iso4217code: String): Locale? {
            if(locales.containsKey(iso4217code))
                return locales[iso4217code]
            var toReturn: Locale? = null
            for (locale in NumberFormat.getAvailableLocales()) {
                val code = NumberFormat.getCurrencyInstance(locale).currency!!.currencyCode
                if (iso4217code == code) {
                    toReturn = locale
                    break
                }
            }
            locales[iso4217code] = toReturn!!
            return toReturn
        }

        fun getCurrencyFormatter(locale:Locale):NumberFormat{
            if(formatters.containsKey(locale))
                return formatters[locale]!!
            val formatter =  NumberFormat.getCurrencyInstance(locale)
            formatters[locale] = formatter
            return formatter
        }
    }
}