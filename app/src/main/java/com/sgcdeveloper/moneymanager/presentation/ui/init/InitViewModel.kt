package com.sgcdeveloper.moneymanager.presentation.ui.init

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.util.isWillBeDouble
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class InitViewModel @Inject constructor(
    private val app: Application,
    private val currencyRepository: CurrencyRepository
) : AndroidViewModel(app) {

    val currencies = currencyRepository.getCurrencies()

    val userName = mutableStateOf("")
    val currency = mutableStateOf(currencies[0])
    val defaultMoney = mutableStateOf("0.0")
    val dialogState = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    fun onEvent(initEvent: InitEvent) {
        when (initEvent) {
            is InitEvent.ChangeCurrency -> {
                currency.value = initEvent.currency
                dialogState.value = DialogState.NoneDialogState
            }
            is InitEvent.ChangeDefaultMoney -> {
                val newMoneyValue = initEvent.newDefaultMoney
                if (newMoneyValue.isWillBeDouble())
                    defaultMoney.value = initEvent.newDefaultMoney
            }
            is InitEvent.ChangeUserName -> {
                userName.value = initEvent.newUserName
            }
            is InitEvent.ShowChangeCurrencyDialog -> {
                dialogState.value = DialogState.SelectCurrenciesDialogState(currency.value.code)
            }
            is InitEvent.CloseDialog -> {
                dialogState.value = DialogState.NoneDialogState
            }
        }
    }
}