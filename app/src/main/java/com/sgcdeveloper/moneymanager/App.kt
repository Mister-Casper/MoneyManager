package com.sgcdeveloper.moneymanager

import android.app.Application
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
        GlobalScope.launch {
            getRecurringTransactionsUseCase.loadTransactions()
        }
    }
}
