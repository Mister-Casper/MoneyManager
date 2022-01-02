package com.sgcdeveloper.moneymanager.data.repository

import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import java.util.*
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor() : CurrencyRepository {

    override fun getCurrencies(): List<String> {
        val currencies = Collections.emptyList<String>()
        Currency.getAvailableCurrencies().forEach { currency ->
            currencies.add(currency.displayName)
        }
        return currencies
    }

}