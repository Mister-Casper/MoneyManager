package com.sgcdeveloper.moneymanager.util

import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.prefa.LoginStatus
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RateUsDialogHelper @Inject constructor(private val appPreferencesHelper: AppPreferencesHelper) {

    var isNeedShow: Boolean = false

    init {
        appPreferencesHelper.setTimesOpen(appPreferencesHelper.getTimesOpen() + 1)
        if (appPreferencesHelper.getFirstTimeOpen().epochMillis == -1L)
            appPreferencesHelper.setFirstTimeOpen(Date(LocalDateTime.now()))
        val reviewStatus = appPreferencesHelper.getReviewStatus()
        val timesOpen = appPreferencesHelper.getTimesOpen()
        val days =
            (Date(LocalDateTime.now()) - appPreferencesHelper.getFirstTimeOpen()).epochMillis / TimeUnit.DAYS.toMillis(1)
        if ((reviewStatus.minTimes <= timesOpen || reviewStatus.minDays <= days) && reviewStatus != ReviewStatus.None && appPreferencesHelper.getLoginStatus() == LoginStatus.None) {
            isNeedShow = true
            if (reviewStatus != ReviewStatus.None) {
                appPreferencesHelper.setReviewStatus(ReviewStatus.values().find { it.id == reviewStatus.id + 1 }!!)
                appPreferencesHelper.setTimesOpen(0)
                appPreferencesHelper.setFirstTimeOpen(Date(LocalDateTime.now()))
            }
        }
    }

    fun rated() {
        appPreferencesHelper.setReviewStatus(ReviewStatus.None)
    }

}