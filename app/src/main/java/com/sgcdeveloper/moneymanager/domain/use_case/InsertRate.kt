package com.sgcdeveloper.moneymanager.domain.use_case

import com.sgcdeveloper.moneymanager.data.db.entry.RateEntry
import com.sgcdeveloper.moneymanager.domain.model.BaseRate
import com.sgcdeveloper.moneymanager.domain.model.Rate
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import javax.inject.Inject

class InsertRate @Inject constructor(private val moneyManagerRepository: MoneyManagerRepository) {
    suspend operator fun invoke(rate:Rate){
        moneyManagerRepository.insertRate(RateEntry(id = rate.id,currency = rate.currency, rate = rate.rate))
    }

    suspend fun insertRates(rates:List<BaseRate>){
        moneyManagerRepository.deleteAllRates()
        moneyManagerRepository.insertRates(rates.map { RateEntry(currency = it.currency, rate = it.rate.toDouble()) })
    }
}