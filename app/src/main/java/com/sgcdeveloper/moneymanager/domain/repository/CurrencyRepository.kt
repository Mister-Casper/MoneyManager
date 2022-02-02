package com.sgcdeveloper.moneymanager.domain.repository

import com.sgcdeveloper.moneymanager.domain.model.Currency

interface CurrencyRepository {
    fun getCurrencies():List<Currency>
}