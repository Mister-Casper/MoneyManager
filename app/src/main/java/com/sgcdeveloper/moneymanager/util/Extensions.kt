package com.sgcdeveloper.moneymanager.util

import com.google.gson.Gson

fun String.isDouble(): Boolean {
    val maybeDouble = this.toDoubleOrNull()
    return (maybeDouble != null)
}

fun String.isWillBeDouble(): Boolean {
    val a1 = this.split(".").size
    val a2 = this.split(",").size == 1
    val a3 = this.split("-").size == 1
    val a4 = this.split(" ").size == 1
    if (a1 <= 2 && a2 && a3 && a4 && (this.split(".")[0].isNotEmpty() || this.isEmpty())) {
        return true
    }
    return false
}

fun Gson.toSafeJson(src: Any?): String? {
    return if (src == null) {
        ""
    } else toJson(src, src.javaClass)
}

fun String.toSafeDouble():Double{
    return if(this == "")
        0.0
    else
        this.toDouble()
}

fun Double.toMoneyString():String{
    return if (this.rem(1) == 0.0)
        this.toLong().toString()
    else
        this.toString()
}
