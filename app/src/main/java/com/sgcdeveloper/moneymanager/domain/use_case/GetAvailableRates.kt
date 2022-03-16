package com.sgcdeveloper.moneymanager.domain.use_case

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import javax.inject.Inject

class GetAvailableRates @Inject constructor(private val moneyManagerRepository: MoneyManagerRepository,private val appPreferencesHelper: AppPreferencesHelper) {
    operator fun invoke(): LiveData<List<Currency>> {
        return Transformations.map(moneyManagerRepository.getRates()) {
            it.map { it.currency }  + appPreferencesHelper.getDefaultCurrency()
        }
    }
}