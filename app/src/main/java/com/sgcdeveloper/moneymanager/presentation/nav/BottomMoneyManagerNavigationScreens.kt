package com.sgcdeveloper.moneymanager.presentation.nav

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.sgcdeveloper.moneymanager.R

sealed class BottomMoneyManagerNavigationScreens(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    object Home : BottomMoneyManagerNavigationScreens("Home", R.string.home, Icons.Filled.Home)
    object Transactions : BottomMoneyManagerNavigationScreens("Transactions", R.string.transaction, Icons.Filled.List)
    object Statistic : BottomMoneyManagerNavigationScreens("Statistic", R.string.statistic, Icons.Filled.CheckCircle)

    companion object {
        fun of(name: String): BottomMoneyManagerNavigationScreens {
            return values().find { it.route == name }!!
        }

        fun values(): List<BottomMoneyManagerNavigationScreens> {
            return listOf(Home, Transactions, Statistic)
        }

        fun getByName(name: String, context: Context): BottomMoneyManagerNavigationScreens {
            return values().find { context.getString(it.resourceId) == name }!!
        }
    }
}