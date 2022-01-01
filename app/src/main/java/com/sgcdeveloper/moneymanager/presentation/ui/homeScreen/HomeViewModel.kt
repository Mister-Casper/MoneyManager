package com.sgcdeveloper.moneymanager.presentation.ui.homeScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val app: Application
) : AndroidViewModel(app) {


    init {

    }

}