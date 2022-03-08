package com.sgcdeveloper.moneymanager.presentation.ui.settings

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.presentation.ui.util.MyEnterPinActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
open class PasswordSettingsViewModel @Inject constructor(
    private val app: Application,
    private val appPreferencesHelper: AppPreferencesHelper
) : AndroidViewModel(app) {

    var isCanChange = mutableStateOf(appPreferencesHelper.getUserPassword())

    fun createPassword(context: Context){
        if(isCanChange.value){
            appPreferencesHelper.setUserPassword(false)
            isCanChange.value = false
        }else {
            changePassword(context)
            appPreferencesHelper.setUserPassword(true)
            isCanChange.value = true
        }
    }

    fun changePassword(context: Context){
        val intent = MyEnterPinActivity.getIntent(context, true)
        context.startActivity(intent)
    }
}