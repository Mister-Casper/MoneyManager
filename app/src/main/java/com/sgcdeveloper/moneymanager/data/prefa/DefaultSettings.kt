package com.sgcdeveloper.moneymanager.data.prefa

import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSettings @Inject constructor() {

    val isSigned: Boolean = false

    init {
        if (Locale.getDefault().country == "US") {

        }
    }
}