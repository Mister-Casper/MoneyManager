package com.sgcdeveloper.moneymanager.data.repository

import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import java.util.*
import java.util.Currency.getInstance
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(private val appPreferencesHelper: AppPreferencesHelper) :
    CurrencyRepository {

    private val currencyLocales: MutableMap<Locale, Currency> = mutableMapOf()

    init {
        for (locale in Locale.getAvailableLocales()) {
            try {
                val currency = getUICurrency(getInstance(locale), locale)
                if (!currencyLocales.values.map { it.code }.contains(currency.code))
                    currencyLocales[locale] = currency
            } catch (e: Exception) {
                // skip strange locale
            }
        }
    }

    override fun getCurrencies(): List<Currency> {
        return currencyLocales.values.sortedBy { it.code }
    }

    override fun getDefaultCurrency(): Currency {
        if (appPreferencesHelper.getDefaultCurrency() != null)
            return appPreferencesHelper.getDefaultCurrency()!!
        val locale = Locale.getDefault()
        val currency = try {
            getInstance(locale)
        }catch (ex:Exception){
            getInstance(Locale.US)
        }
        return getUICurrency(currency, locale)
    }

    private fun getUICurrency(currency: java.util.Currency, locale: Locale): Currency {
        val symbol = currency.getSymbol(locale)
        return Currency(
            currency.currencyCode,
            String.format("%s - %s (%s)", currency.currencyCode, currency.displayName, symbol),
            symbol
        )
    }

}