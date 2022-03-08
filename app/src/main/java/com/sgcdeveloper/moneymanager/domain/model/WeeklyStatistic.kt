package com.sgcdeveloper.moneymanager.domain.model


data class WeeklyStatistic (val dayItems:List<DayStatistic>,val sum:String,val title:String,val labels:List<String>,val rowColor: Int)