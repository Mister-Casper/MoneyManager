package com.sgcdeveloper.moneymanager.presentation.ui.addWallet

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.use_case.GetWallets
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_colors
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitViewModel
import com.sgcdeveloper.moneymanager.util.isWillBeDouble
import com.sgcdeveloper.moneymanager.util.wallet_icons
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
open class AddWalletViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val currencyRepository: CurrencyRepository
) : AndroidViewModel(app) {
    val walletName = mutableStateOf("")
    val walletCurrency = mutableStateOf(appPreferencesHelper.getDefaultCurrency())
    val walletMoney = mutableStateOf("")
    val walletColor = mutableStateOf(wallet_colors[0].toArgb())
    val walletIcon = mutableStateOf(R.drawable.wallet_icon_1)

    val wallet = mutableStateOf(Wallet(icon = walletIcon.value, currency = walletCurrency.value))

    val dialogState = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    val currencies = currencyRepository.getCurrencies()

    init {
        formatMoney(walletMoney.value)
    }

    fun onEvent(walletEvent: WalletEvent) {
        when (walletEvent) {
            is WalletEvent.SetWallet -> {
                val wallet = walletEvent.wallet
                walletName.value = wallet.name
                walletCurrency.value = wallet.currency
                walletMoney.value = wallet.money
                walletColor.value = wallet.color
                walletIcon.value = wallet.icon
                this.wallet.value = wallet
            }
            is WalletEvent.ChangeWalletName -> {
                if (walletEvent.name.length <= InitViewModel.MAX_WALLET_NAME_LENGTH || walletEvent.name.length <= walletName.value.length) {
                    walletName.value = walletEvent.name
                    this.wallet.value = this.wallet.value.copy(name = walletEvent.name)
                }
            }
            is WalletEvent.ShowChangeCurrencyDialog -> {
                dialogState.value = DialogState.SelectCurrenciesDialogState
            }
            is WalletEvent.ChangeCurrency -> {
                walletCurrency.value = walletEvent.currency
                this.wallet.value = this.wallet.value.copy(currency = walletEvent.currency)
                dialogState.value = DialogState.NoneDialogState
                formatMoney(walletMoney.value)
            }
            is WalletEvent.CloseDialog -> {
                dialogState.value = DialogState.NoneDialogState
            }
            is WalletEvent.ChangeMoney -> {
                val newMoneyValue = walletEvent.money
                if ((newMoneyValue.isWillBeDouble() && newMoneyValue.length <= InitViewModel.MAX_MONEY_LENGTH) || newMoneyValue.length <= walletMoney.value.length) {
                    walletMoney.value = walletEvent.money
                    formatMoney(walletEvent.money)
                }
            }
            is WalletEvent.ChangeColor -> {
                walletColor.value = walletEvent.color
                this.wallet.value = this.wallet.value.copy(color = walletEvent.color)
            }
            is WalletEvent.ChangeIcon -> {
                walletIcon.value = walletEvent.icon
                this.wallet.value = this.wallet.value.copy(icon = wallet_icons[walletEvent.icon])
            }
            is WalletEvent.Clear -> {
                walletName.value = ""
                walletCurrency.value = appPreferencesHelper.getDefaultCurrency()
                walletMoney.value = ""
                walletColor.value = 0
                walletIcon.value = R.drawable.wallet_icon_1
                wallet.value = Wallet(icon = walletIcon.value, currency = walletCurrency.value)
            }
            is WalletEvent.InsertWallet -> {
                viewModelScope.launch {
                    wallet.value = wallet.value.copy(money = walletMoney.value)
                    walletsUseCases.insertWallet(wallet.value)
                }
            }
        }
    }

    private fun formatMoney(money: String) {
        val formatter =
            NumberFormat.getCurrencyInstance(GetWallets.getLocalFromISO(walletCurrency.value.code)!!)
        this.wallet.value =
            this.wallet.value.copy(formattedMoney = formatter.format(money.toDoubleOrNull() ?: 0))
    }

}