package com.sgcdeveloper.moneymanager.presentation.ui.homeScreen

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ItemPosition
import org.burnoutcrew.reorderable.move
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases
) : AndroidViewModel(app) {
    lateinit var wallets: LiveData<List<Wallet>>
    var existWallets = mutableStateListOf<Wallet>()

    init {
        viewModelScope.launch {
            wallets = walletsUseCases.getWallets.getUIWallets()
            walletsUseCases.getWallets().observeForever {
                existWallets.clear()
                existWallets.addAll(it.toMutableStateList())
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            existWallets.mapIndexed { ix, wallet -> wallet.also { wallet.order = ix.toLong() } }
            walletsUseCases.insertWallet.insertWallets(existWallets)
        }
    }

    fun move(from: ItemPosition, to: ItemPosition) {
        existWallets.move(from.index, to.index)
    }

    fun deleteWallet(wallet:Wallet){
        viewModelScope.launch {
            walletsUseCases.deleteWallet(wallet.walletId)
        }
    }
}