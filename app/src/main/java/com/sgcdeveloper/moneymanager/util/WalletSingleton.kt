package com.sgcdeveloper.moneymanager.util

import androidx.lifecycle.MutableLiveData
import com.sgcdeveloper.moneymanager.domain.model.Wallet

object WalletSingleton {
    private val observers:ArrayList<WalletChangerListener> = ArrayList()

    fun addObserver(observer:WalletChangerListener){
        observers.add(observer)
    }

    var wallet: MutableLiveData<Wallet> = MutableLiveData()

    fun setWallet(value:Wallet) {
        wallet.value = value
        observers.forEach{
            it.walletChanged()
        }
    }
}

interface WalletChangerListener{
    fun walletChanged()
}