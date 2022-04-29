package com.sgcdeveloper.moneymanager

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.sgcdeveloper.moneymanager.domain.use_case.GetRecurringTransactionsUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
open class App : Application() {

    @Inject
    lateinit var getRecurringTransactionsUseCase: GetRecurringTransactionsUseCase

    override fun onCreate() {
        super.onCreate()
        try {
            MobileAds.initialize(this) { }
        }catch (e:Exception) {

        }
        GlobalScope.launch {
            getRecurringTransactionsUseCase.loadTransactions()
        }
    }
}
