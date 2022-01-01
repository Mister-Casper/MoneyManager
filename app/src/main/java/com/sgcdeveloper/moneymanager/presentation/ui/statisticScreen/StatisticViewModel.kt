package com.sgcdeveloper.moneymanager.presentation.ui.statisticScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class StatisticViewModel @Inject constructor(
    private val app: Application
) : AndroidViewModel(app) {


    init {

    }

}