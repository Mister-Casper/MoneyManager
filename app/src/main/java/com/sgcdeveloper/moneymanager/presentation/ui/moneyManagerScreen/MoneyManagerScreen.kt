package com.sgcdeveloper.moneymanager.presentation.ui.moneyManagerScreen

import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionEvent
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionScreen
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.addWallet.AddWalletScreen
import com.sgcdeveloper.moneymanager.presentation.ui.addWallet.AddWalletViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.addWallet.WalletEvent
import com.sgcdeveloper.moneymanager.presentation.ui.homeScreen.HomeScreen
import com.sgcdeveloper.moneymanager.presentation.ui.homeScreen.HomeViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.statistic.StatisticScreen
import com.sgcdeveloper.moneymanager.presentation.ui.statistic.StatisticViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions.TimeIntervalTransactionEvent
import com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions.TimeIntervalTransactionsScreen
import com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions.TimeIntervalTransactionsViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.transactions.TransactionsScreen
import com.sgcdeveloper.moneymanager.presentation.ui.transactions.TransactionsViewModel
import com.sgcdeveloper.moneymanager.util.TimeInternalSingleton

@Composable
fun MoneyManagerScreen(
    moneyManagerViewModel: MoneyManagerViewModel,
    homeViewModel: HomeViewModel,
    addTransactionViewModel: AddTransactionViewModel,
    statisticViewModel: StatisticViewModel,
    addWalletViewModel: AddWalletViewModel,
    transactionsViewModel: TransactionsViewModel,
    timeIntervalTransactionsViewModel: TimeIntervalTransactionsViewModel
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
        MainScreenNavigationConfigurations(
            navController,
            addTransactionViewModel,
            homeViewModel,
            statisticViewModel,
            addWalletViewModel,
            transactionsViewModel,
            timeIntervalTransactionsViewModel
        )
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

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    addTransactionViewModel: AddTransactionViewModel,
    homeViewModel: HomeViewModel,
    statisticViewModel: StatisticViewModel,
    addWalletViewModel: AddWalletViewModel,
    transactionsViewModel: TransactionsViewModel,
    timeIntervalTransactionsViewModel: TimeIntervalTransactionsViewModel
) {
    NavHost(
        navController,
        startDestination = BottomMoneyManagerNavigationScreens.Transactions.route,
    ) {
        composable(BottomMoneyManagerNavigationScreens.Home.route) {
            HomeScreen(homeViewModel, navController)
        }
        composable(BottomMoneyManagerNavigationScreens.Transactions.route) {
            TransactionsScreen(transactionsViewModel, navController)
        }
        composable(BottomMoneyManagerNavigationScreens.Statistic.route) {
            StatisticScreen(statisticViewModel, navController)
        }
        composable(Screen.AddTransaction(null).route + "{wallet}") { backStackEntry ->
            val wallet =
                Gson().fromJson(backStackEntry.arguments?.getString("wallet"), Wallet::class.java)

            addTransactionViewModel.onEvent(AddTransactionEvent.SetDefaultWallet(wallet))
            AddTransactionScreen(addTransactionViewModel, navController)
        }

        composable(Screen.EditTransaction(null).route + "{transaction}") { backStackEntry ->
            val transaction =
                Gson().fromJson(backStackEntry.arguments?.getString("transaction"), TransactionEntry::class.java)

            if (transaction != null)
                addTransactionViewModel.onEvent(AddTransactionEvent.SetExistTransaction(transaction))
            AddTransactionScreen(addTransactionViewModel, navController)

            backStackEntry.arguments?.putString("transaction", "")
        }
        composable(Screen.AddWallet(null).route + "{wallet}") { backStackEntry ->
            val wallet =
                Gson().fromJson(backStackEntry.arguments?.getString("wallet"), Wallet::class.java)
            if (wallet != null)
                addWalletViewModel.onEvent(WalletEvent.SetWallet(wallet))
            AddWalletScreen(navController, addWalletViewModel)

            backStackEntry.arguments?.putString("wallet", "")
        }

        composable(Screen.TimeIntervalTransactions(null).route + "{wallet}") { backStackEntry ->
            val walletJson = backStackEntry.arguments?.getString("wallet")
            val wallet = Gson().fromJson(walletJson, Wallet::class.java)
            timeIntervalTransactionsViewModel.onEvent(TimeIntervalTransactionEvent.SetDefaultWallet(wallet))
            if (TimeInternalSingleton.timeIntervalController != null) {
                timeIntervalTransactionsViewModel.onEvent(
                    TimeIntervalTransactionEvent.ChangeTimeInterval(
                        TimeInternalSingleton.timeIntervalController!!
                    )
                )
                TimeInternalSingleton.timeIntervalController = null
            }

            TimeIntervalTransactionsScreen(timeIntervalTransactionsViewModel, navController)
        }
    }
}