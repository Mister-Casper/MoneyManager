package com.sgcdeveloper.moneymanager.presentation.ui.homeScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases
) : AndroidViewModel(app) {
    lateinit var wallets: LiveData<List<Wallet>>

    init {
        viewModelScope.launch {
            wallets = walletsUseCases.getWallets()
        }
    }

    fun onEvent(homeEvent: HomeEvent) {
        when (homeEvent) {

        }
    }

}