package com.sgcdeveloper.moneymanager.presentation.ui.moneyManagerScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class MoneyManagerViewModel @Inject constructor(
    private val app: Application
) : AndroidViewModel(app) {


    init {

    }

}