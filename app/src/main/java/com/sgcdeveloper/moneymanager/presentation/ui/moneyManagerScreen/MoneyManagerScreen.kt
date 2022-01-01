package com.sgcdeveloper.moneymanager.presentation.ui.moneyManagerScreen

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import com.sgcdeveloper.moneymanager.presentation.ui.homeScreen.HomeScreen
import com.sgcdeveloper.moneymanager.presentation.ui.homeScreen.HomeViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.statisticScreen.StatisticScreen
import com.sgcdeveloper.moneymanager.presentation.ui.statisticScreen.StatisticViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.transactionScreen.TransactionScreen
import com.sgcdeveloper.moneymanager.presentation.ui.transactionScreen.TransactionViewModel

@Composable
fun MoneyManagerScreen(
    moneyManagerViewModel: MoneyManagerViewModel,
    homeViewModel: HomeViewModel,
    transactionViewModel: TransactionViewModel,
    statisticViewModel: StatisticViewModel
) {
    val navController = rememberNavController()
    val bottomNavigationItems = listOf(
        BottomMoneyManagerNavigationScreens.Home,
        BottomMoneyManagerNavigationScreens.Transactions,
        BottomMoneyManagerNavigationScreens.Statistic
    )
    Scaffold(
        bottomBar = { SpookyAppBottomNavigation(navController, bottomNavigationItems) }
    ) {
        MainScreenNavigationConfigurations(navController,transactionViewModel,homeViewModel,statisticViewModel)
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun SpookyAppBottomNavigation(
    navController: NavHostController,
    items: List<BottomMoneyManagerNavigationScreens>
) {
    BottomNavigation() {
        items.forEach { screen ->
            val currentRoute = currentRoute(navController)
            val title = stringResource(id = screen.resourceId)
            BottomNavigationItem(
                icon = { Icon(screen.icon, title) },
                label = { Text(title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route)
                    }
                }
            )
        }
    }
}

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    homeViewModel: HomeViewModel,
    statisticViewModel: StatisticViewModel
) {
    NavHost(
        navController,
        startDestination = BottomMoneyManagerNavigationScreens.Transactions.route,
    ) {
        composable(BottomMoneyManagerNavigationScreens.Home.route) {
            HomeScreen(homeViewModel)
        }
        composable(BottomMoneyManagerNavigationScreens.Transactions.route) {
            TransactionScreen(transactionViewModel)
        }
        composable(BottomMoneyManagerNavigationScreens.Statistic.route) {
            StatisticScreen(statisticViewModel)
        }
    }
}