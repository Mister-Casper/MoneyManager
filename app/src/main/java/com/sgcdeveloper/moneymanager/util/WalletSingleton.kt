package com.sgcdeveloper.moneymanager.util

import androidx.lifecycle.MutableLiveData
import com.sgcdeveloper.moneymanager.domain.model.Wallet

object WalletSingleton {
    private val observers:ArrayList<WalletChangerListener> = ArrayList()

    fun addObserver(observer:WalletChangerListener){
        observers.add(observer)
    }

    var wallet: MutableLiveData<Wallet> = MutableLiveData()

    fun setWallet(value:Wallet?) {
        wallet.value = value
        observers.forEach{
            it.walletChanged(value)
        }
    }

    fun postWallet(value:Wallet?) {
        wallet.postValue(value)
        observers.forEach{
            it.walletChanged(value)
        }
    }
}

interface WalletChangerListener{
    fun walletChanged(newWallet:Wallet?=null)
}