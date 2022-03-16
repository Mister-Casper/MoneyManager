package com.sgcdeveloper.moneymanager.domain.use_case

import com.sgcdeveloper.moneymanager.data.db.entry.RateEntry
import com.sgcdeveloper.moneymanager.domain.model.Rate
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import javax.inject.Inject

class InsertRate @Inject constructor(private val moneyManagerRepository: MoneyManagerRepository) {
    suspend operator fun invoke(rate:Rate){
        moneyManagerRepository.insertRate(RateEntry(currency = rate.currency, rate = rate.rate))
    }
}