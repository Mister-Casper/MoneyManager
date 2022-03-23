package com.sgcdeveloper.moneymanager.presentation.ui.addWallet

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.domain.model.BaseRate
import com.sgcdeveloper.moneymanager.domain.model.Currency
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.use_case.GetAvailableRates
import com.sgcdeveloper.moneymanager.domain.use_case.GetWallets
import com.sgcdeveloper.moneymanager.domain.use_case.InsertRate
import com.sgcdeveloper.moneymanager.domain.use_case.WalletsUseCases
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_colors
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DialogState
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitViewModel
import com.sgcdeveloper.moneymanager.util.isWillBeDouble
import com.sgcdeveloper.moneymanager.util.wallet_icons
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class AddWalletViewModel @Inject constructor(
    private val app: Application,
    private val walletsUseCases: WalletsUseCases,
    private val getAvailableRates: GetAvailableRates,
    private val insertRate: InsertRate,
    private val appPreferencesHelper: AppPreferencesHelper,
    private val currencyRepository: CurrencyRepository
) : AndroidViewModel(app) {
    val walletName = mutableStateOf("")
    val walletCurrency = mutableStateOf(appPreferencesHelper.getDefaultCurrency())
    val walletMoney = mutableStateOf("")
    val walletColor = mutableStateOf(wallet_colors[0].toArgb())
    val walletIcon = mutableStateOf(R.drawable.wallet_icon_1)

    val wallet = MutableLiveData<Wallet>()

    val dialogState = mutableStateOf<DialogState>(DialogState.NoneDialogState)

    val currencies = currencyRepository.getCurrencies()
    val back = mutableStateOf(false)
    val backDialog = mutableStateOf(false)

    var isEditingMode = mutableStateOf(false)

    var availableCurrencies: List<Currency> = Collections.emptyList()
    var availableRates = MutableLiveData<List<BaseRate>>()

    val defaultCurrency = currencyRepository.getDefaultCurrency()

    init {
        wallet.value = Wallet(icon = walletIcon.value, currency = walletCurrency.value, order = 0)
        formatMoney(walletMoney.value)
        getAvailableRates().observeForever {
            availableCurrencies = it.map { it.currency }
        }
        getAvailableRates.getBaseRates().observeForever {
            availableRates.value = it
        }
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
                isEditingMode.value = (wallet.walletId != 0L)
            }
            is WalletEvent.ChangeWalletName -> {
                if (walletEvent.name.length <= InitViewModel.MAX_WALLET_NAME_LENGTH || walletEvent.name.length <= walletName.value.length) {
                    walletName.value = walletEvent.name
                    this.wallet.value = this.wallet.value!!.copy(name = walletEvent.name)
                }
            }
            is WalletEvent.ShowChangeCurrencyDialog -> {
                dialogState.value = DialogState.SelectCurrenciesDialogState
            }
            is WalletEvent.ChangeCurrency -> {
                if (!availableCurrencies.contains(walletEvent.currency)) {
                    dialogState.value = DialogState.AddCurrencyRateDialog(walletEvent.currency)
                } else {
                 changeCurrency(walletEvent.currency)
                    dialogState.value = DialogState.NoneDialogState
                }
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
                this.wallet.value = this.wallet.value!!.copy(color = walletEvent.color)
            }
            is WalletEvent.ChangeIcon -> {
                walletIcon.value = walletEvent.icon
                this.wallet.value = this.wallet.value!!.copy(icon = wallet_icons[walletEvent.icon])
            }
            is WalletEvent.InsertWallet -> {
                viewModelScope.launch {
                    wallet.value = wallet.value!!.copy(money = walletMoney.value)
                    walletsUseCases.insertWallet(wallet.value!!)
                }
            }
            is WalletEvent.ShowDeleteWalletDialog -> {
                if (wallet.value!!.isDefault) {
                    dialogState.value = DialogState.InformDialog(app.getString(R.string.cant_delete_default_wallet))
                } else
                    dialogState.value = DialogState.DeleteWalletDialog
            }
            is WalletEvent.DeleteWallet -> {
                dialogState.value = DialogState.NoneDialogState
                viewModelScope.launch {
                    walletsUseCases.deleteWallet(wallet.value!!.walletId)
                }
            }
            is WalletEvent.AddCurrency -> {
                viewModelScope.launch {
                    insertRate(walletEvent.rate)
                    changeCurrency(walletEvent.rate.currency)
                }
            }
        }
    }

    private fun changeCurrency(currency: Currency) {
        walletCurrency.value = currency
        this.wallet.value = this.wallet.value!!.copy(currency = currency)
        dialogState.value = DialogState.NoneDialogState
        formatMoney(walletMoney.value)
        dialogState.value = DialogState.NoneDialogState
    }

    private fun formatMoney(money: String) {
        val formatter =
            NumberFormat.getCurrencyInstance(GetWallets.getLocalFromISO(walletCurrency.value.code)!!)
        this.wallet.value =
            this.wallet.value!!.copy(formattedMoney = formatter.format(money.toDoubleOrNull() ?: 0))
    }

    fun saveRates(rates: List<BaseRate>) {
        viewModelScope.launch {
            insertRate.insertRates(rates)
        }
    }
}