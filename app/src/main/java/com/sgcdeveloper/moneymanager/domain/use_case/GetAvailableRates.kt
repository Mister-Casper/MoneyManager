package com.sgcdeveloper.moneymanager.domain.use_case

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.BaseRate
import com.sgcdeveloper.moneymanager.domain.model.Rate
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.util.deleteUselessZero
import javax.inject.Inject

class GetAvailableRates @Inject constructor(
    private val moneyManagerRepository: MoneyManagerRepository,
    private val appPreferencesHelper: AppPreferencesHelper
) {
    operator fun invoke(): LiveData<List<Rate>> {
        return Transformations.map(moneyManagerRepository.getRates()) {
            it.map { Rate(it.currency, it.rate) } + Rate(appPreferencesHelper.getDefaultCurrency()!!, 1.0)
        }
    }

    fun getBaseRates(): LiveData<List<BaseRate>> {
        return Transformations.map(moneyManagerRepository.getRates()) {
            it.map { BaseRate(it.currency, it.rate.deleteUselessZero()) }
        }
    }
}