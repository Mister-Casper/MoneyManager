package com.sgcdeveloper.moneymanager.data.repository

import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import java.util.*
import java.util.Currency.getAvailableCurrencies
import java.util.Currency.getInstance
import javax.inject.Inject


class CurrencyRepositoryImpl @Inject constructor() : CurrencyRepository {

    private val currencyLocales: MutableMap<java.util.Currency, Locale> = mutableMapOf()

    init {
        for (locale in Locale.getAvailableLocales()) {
            try {
                if (locale != null) {
                    val currency = getInstance(locale)
                    currencyLocales[currency] = locale
                }
            } catch (e: Exception) {
                // skip strange locale
            }
        }
    }

    override fun getCurrencies(): List<Currency> {
        val currencies = mutableListOf<Currency>()
        getAvailableCurrencies().forEach { currency ->
            if (!currency.displayName.matches(Regex(".*\\d.*"))) {
                currencies.add(
                    Currency(
                        currency.currencyCode,
                        currency.currencyCode + " - " + currency.displayName + " (" + getSymbol(currency) + ")"
                    )
                )
            }
        }
        currencies.sortBy { it.code }
        return currencies
    }

    private fun getSymbol(currency: java.util.Currency): String {
        val locale = if (currencyLocales.containsKey(currency))
            currencyLocales[currency]
        else
            Locale.getDefault()
        return currency.getSymbol(locale)
    }

}