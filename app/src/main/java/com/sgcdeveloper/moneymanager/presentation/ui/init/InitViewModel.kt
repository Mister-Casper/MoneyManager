package com.sgcdeveloper.moneymanager.presentation.ui.init

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.prefa.LoginStatus
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.util.isDouble
import com.sgcdeveloper.moneymanager.util.isWillBeDouble
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class InitViewModel @Inject constructor(
    private val app: Application,
    private val currencyRepository: CurrencyRepository,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val moneyManagerRepository: MoneyManagerRepository
) : AndroidViewModel(app) {

    val currencies = currencyRepository.getCurrencies()

    val userName = mutableStateOf(appPreferencesHelper.getUserNAme())
    val currency = mutableStateOf(currencyRepository.getDefaultCurrency())
    val defaultMoney = mutableStateOf("0.0")
    val defaultWalletName = mutableStateOf(app.getString(R.string.wallet_number, 1))
    val dialogState = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    val isMoveNext = mutableStateOf(false)
    val isNextEnable = mutableStateOf(userName.value.isNotEmpty())

    fun onEvent(initEvent: InitEvent) {
        when (initEvent) {
            is InitEvent.ChangeCurrency -> {
                currency.value = initEvent.currency
                dialogState.value = DialogState.NoneDialogState
            }
            is InitEvent.ChangeDefaultMoney -> {
                val newMoneyValue = initEvent.newDefaultMoney
                if ((newMoneyValue.isWillBeDouble() && newMoneyValue.length <= MAX_MONEY_LENGTH)||newMoneyValue.length <= defaultMoney.value.length)
                    defaultMoney.value = initEvent.newDefaultMoney
            }
            is InitEvent.ChangeUserName -> {
                if (initEvent.newUserName.length <= MAX_USER_NAME_LENGTH || initEvent.newUserName.length <= userName.value.length)
                    userName.value = initEvent.newUserName
            }
            is InitEvent.ShowChangeCurrencyDialog -> {
                dialogState.value = DialogState.SelectCurrenciesDialogState
            }
            is InitEvent.CloseDialog -> {
                dialogState.value = DialogState.NoneDialogState
            }
            is InitEvent.Next -> {
                initNewAccount()
                appPreferencesHelper.setLoginStatus(LoginStatus.None)
                isMoveNext.value = true
            }
            is InitEvent.ChangeDefaultWalletName -> {
                if (initEvent.newDefaultWalletName.length <= MAX_WALLET_NAME_LENGTH || initEvent.newDefaultWalletName.length <= defaultWalletName.value.length)
                    defaultWalletName.value = initEvent.newDefaultWalletName
            }
        }
        isNextEnable.value = (userName.value.isNotEmpty() && defaultWalletName.value.isNotEmpty() && defaultMoney.value.isDouble())
    }

    private fun initNewAccount() {
        viewModelScope.launch {
            appPreferencesHelper.setUserName(userName.value)
            appPreferencesHelper.setDefaultCurrency(currency.value)
            val firstWallet = WalletEntry(name = defaultWalletName.value, money = defaultMoney.value.toDouble(), currency = currency.value)
            moneyManagerRepository.insertWallet(firstWallet)
        }
    }

    companion object {
        const val MAX_USER_NAME_LENGTH = 24
        const val MAX_MONEY_LENGTH = 16
        const val MAX_WALLET_NAME_LENGTH = 12
    }
}