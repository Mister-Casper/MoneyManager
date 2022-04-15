package com.sgcdeveloper.moneymanager.util

enum class ReviewStatus(val id:Int,val minTimes:Int,val minDays:Int) {
    First(0,5,3),
    Second(1,15,15),
    Third(2,30,30),
    None(3,0,0)
}