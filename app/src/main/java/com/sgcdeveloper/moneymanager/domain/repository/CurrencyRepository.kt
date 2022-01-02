package com.sgcdeveloper.moneymanager.domain.repository

interface CurrencyRepository {
    fun getCurrencies():List<String>
}