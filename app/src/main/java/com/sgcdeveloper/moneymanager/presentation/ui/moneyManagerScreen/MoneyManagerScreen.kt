package com.sgcdeveloper.moneymanager.presentation.ui.moneyManagerScreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sgcdeveloper.moneymanager.presentation.main.MainViewModel
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import com.sgcdeveloper.moneymanager.presentation.ui.homeScreen.HomeScreen
import com.sgcdeveloper.moneymanager.presentation.ui.homeScreen.HomeViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.statistic.StatisticScreen
import com.sgcdeveloper.moneymanager.presentation.ui.transactions.TransactionsScreen

@Composable
fun MoneyManagerScreen(globalNavController: NavHostController,mainViewModel:MainViewModel) {
    val navController = rememberNavController()
    val bottomNavigationItems = listOf(
        BottomMoneyManagerNavigationScreens.Home,
        BottomMoneyManagerNavigationScreens.Transactions,
        BottomMoneyManagerNavigationScreens.Statistic
    )
    Scaffold(
        bottomBar = { SpookyAppBottomNavigation(navController, bottomNavigationItems) }
    ) {
        MainScreenNavigationConfigurations(navController, globalNavController,mainViewModel)
    }
    BackHandler {
        // Ignore
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    globalNavController: NavController,
    mainViewModel:MainViewModel
) {
    NavHost(
        navController,
        startDestination = mainViewModel.defaultStartupScreen.value.route,
    ) {
        composable(BottomMoneyManagerNavigationScreens.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            LaunchedEffect(Unit){
                homeViewModel.loadBudgets()
            }
            HomeScreen(homeViewModel, globalNavController)
        }
        composable(BottomMoneyManagerNavigationScreens.Transactions.route) {
            TransactionsScreen(hiltViewModel(), globalNavController)
        }
        composable(BottomMoneyManagerNavigationScreens.Statistic.route) {
            StatisticScreen(hiltViewModel(), globalNavController)
        }
    }
}