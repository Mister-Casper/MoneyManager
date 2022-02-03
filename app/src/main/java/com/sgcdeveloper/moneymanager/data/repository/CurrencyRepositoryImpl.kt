package com.sgcdeveloper.moneymanager.data.repository

import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import java.util.*
import java.util.Currency.getInstance
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor() : CurrencyRepository {

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
        return getUICurrency(getInstance(Locale.getDefault()), Locale.getDefault())
    }

    private fun getUICurrency(currency: java.util.Currency, locale: Locale): Currency {
        return Currency(
            currency.currencyCode,
            String.format("%s - %s (%s)", currency.currencyCode, currency.displayName, currency.getSymbol(locale))
        )
    }

}