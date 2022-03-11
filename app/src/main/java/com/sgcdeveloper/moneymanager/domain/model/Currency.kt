package com.sgcdeveloper.moneymanager.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Currency (val code:String,val name:String,val symbol:String) : Parcelable
