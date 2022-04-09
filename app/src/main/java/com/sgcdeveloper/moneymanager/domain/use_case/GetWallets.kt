package com.sgcdeveloper.moneymanager.domain.use_case

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.db.entry.RateEntry
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
import com.sgcdeveloper.moneymanager.domain.model.AddNewWallet
import com.sgcdeveloper.moneymanager.domain.model.AllWallets
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.util.deleteUselessZero
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

class GetWallets @Inject constructor(
    private val context: Context,
    private val moneyManagerRepository: MoneyManagerRepository,
    private val currencyRepository: CurrencyRepository
) {
    operator fun invoke(): LiveData<MutableList<Wallet>> {
        return Transformations.map(moneyManagerRepository.getWallets()) {
            transformWallets(it)
        }
    }

    fun getWallets(): List<Wallet> {
        return transformWallets(moneyManagerRepository.getWalletsOnce())
    }

    fun getAllUIWallets(): LiveData<List<Wallet>> {
        return Transformations.switchMap(moneyManagerRepository.getWallets()) { wallets ->
            Transformations.map(moneyManagerRepository.getRates()) { rates ->
                listOf(
                    getAllMoney(
                        wallets,
                        rates
                    )
                ).plus(transformWallets(wallets).plus(AddNewWallet(currencyRepository.getDefaultCurrency())))
            }
        }
    }

    fun getUIWallets(): LiveData<List<Wallet>> {
        return Transformations.map(moneyManagerRepository.getWallets()) {
            transformWallets(it).plus(AddNewWallet(currencyRepository.getDefaultCurrency()))
        }
    }

    suspend fun getUIWalletsOnce(): List<Wallet> {
        return transformWallets(moneyManagerRepository.getAsyncWallets()).plus(AddNewWallet(currencyRepository.getDefaultCurrency()))
    }

    suspend fun getWallet(id: Long): Wallet {
          return transformWallet(moneyManagerRepository.getWallet(id))
    }

    private fun transformWallets(wallets: List<WalletEntry>): MutableList<Wallet> {
        return wallets.map { wallet -> transformWallet(wallet) }.sortedBy { it.order }.toMutableList()
    }

    private fun transformWallet(wallet: WalletEntry?): Wallet {
        if(wallet == null)
            return Wallet(currency = com.sgcdeveloper.moneymanager.domain.model.Currency("","",""))
        val formatter = NumberFormat.getCurrencyInstance(getLocalFromISO(wallet.currency.code)!!)
        var money = wallet.money.deleteUselessZero()
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
            wallet.currency,
            wallet.order
        )
    }

    private fun getDrawable(name: String): Int {
        val resources: Resources = context.resources
        return resources.getIdentifier(name, "drawable", context.packageName)
    }

    private fun getAllMoney(wallets: List<WalletEntry>, rates: List<RateEntry>): AllWallets {
        var money = 0.0
        val formatter =
            NumberFormat.getCurrencyInstance(getLocalFromISO(currencyRepository.getDefaultCurrency().code)!!)
        wallets.forEach { wallet ->
            money += if (rates.map { it.currency }.contains(wallet.currency)) {
                val rateMoney = wallet.money / rates.find { it.currency == wallet.currency }!!.rate
                rateMoney
            } else {
                wallet.money
            }
        }
        var formattedMoney = formatter.format(money)

        if (formattedMoney == "0.0" || formattedMoney == "0")
            formattedMoney = ""

        return AllWallets(
            context.getString(R.string.all_wallets),
            formattedMoney,
            currencyRepository.getDefaultCurrency()
        )
    }

    companion object {
        val df = DecimalFormat("#.##")

        private val locales = HashMap<String, Locale>()
        private val formatters = HashMap<Locale, NumberFormat>()

        fun getLocalFromISO(iso4217code: String): Locale? {
            if (locales.containsKey(iso4217code))
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

        fun getCurrencyFormatter(locale: Locale): NumberFormat {
            if (formatters.containsKey(locale))
                return formatters[locale]!!
            val formatter = NumberFormat.getCurrencyInstance(locale)
            formatters[locale] = formatter
            return formatter
        }
    }
}