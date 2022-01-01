package com.sgcdeveloper.moneymanager.presentation.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.sgcdeveloper.moneymanager.R

sealed class BottomMoneyManagerNavigationScreens(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Home : BottomMoneyManagerNavigationScreens("Home", R.string.home, Icons.Filled.Home)
    object Transactions : BottomMoneyManagerNavigationScreens("Transactions", R.string.transaction, Icons.Filled.List)
    object Statistic : BottomMoneyManagerNavigationScreens("Statistic", R.string.statistic, Icons.Filled.CheckCircle)
}