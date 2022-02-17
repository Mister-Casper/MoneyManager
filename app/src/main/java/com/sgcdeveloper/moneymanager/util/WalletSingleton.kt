package com.sgcdeveloper.moneymanager.util

import androidx.lifecycle.MutableLiveData
import com.sgcdeveloper.moneymanager.domain.model.Wallet

object WalletSingleton {
    var wallet: MutableLiveData<Wallet> = MutableLiveData()
}