package com.sgcdeveloper.moneymanager.presentation.ui.init

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.prefa.LoginStatus
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_color_6
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.util.SyncHelper
import com.sgcdeveloper.moneymanager.util.isWillBeDouble
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
open class InitViewModel @Inject constructor(
    private val app: Application,
    private val currencyRepository: CurrencyRepository,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val moneyManagerRepository: MoneyManagerRepository,
    private val syncHelper: SyncHelper
) : AndroidViewModel(app) {

    val currencies = currencyRepository.getCurrencies()

    val userName = mutableStateOf(appPreferencesHelper.getUserNAme())
    val currency = mutableStateOf(currencyRepository.getDefaultCurrency())
    val defaultMoney = mutableStateOf("")
    val defaultWalletName = mutableStateOf(app.getString(R.string.cash_wallet))
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
                if ((newMoneyValue.isWillBeDouble() && newMoneyValue.length <= MAX_MONEY_LENGTH) || newMoneyValue.length <= defaultMoney.value.length)
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
                appPreferencesHelper.setLoginStatus(LoginStatus.None)
                initNewAccount()
                isMoveNext.value = true
            }
            is InitEvent.ChangeDefaultWalletName -> {
                if (initEvent.newDefaultWalletName.length <= MAX_WALLET_NAME_LENGTH || initEvent.newDefaultWalletName.length <= defaultWalletName.value.length)
                    defaultWalletName.value = initEvent.newDefaultWalletName
            }
        }
        isNextEnable.value = (userName.value.isNotEmpty() && defaultWalletName.value.isNotEmpty())
    }

    private fun initNewAccount() {
        appPreferencesHelper.setUserName(userName.value)
        appPreferencesHelper.setDefaultCurrency(currency.value)

        runBlocking {
            moneyManagerRepository.deleteAllWallets()
            moneyManagerRepository.deleteAllTransactions()

            val firstWallet = WalletEntry(
                isDefault = true,
                name = defaultWalletName.value,
                money = defaultMoney.value.toDoubleOrNull() ?: 0.0,
                currency = currency.value,
                color = wallet_color_6.toArgb(),
                order = 1
            )
            moneyManagerRepository.insertWallet(firstWallet)

            syncHelper.syncServerData()
        }
    }

    companion object {
        const val MAX_USER_NAME_LENGTH = 24
        const val MAX_MONEY_LENGTH = 16
        const val MAX_WALLET_NAME_LENGTH = 12
        const val MAX_RATE_LENGTH = 9
        const val MAX_DESCRIPTION_SIZE = 20
    }
}