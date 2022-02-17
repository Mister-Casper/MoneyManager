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
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.presentation.nav.BottomMoneyManagerNavigationScreens
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionEvent
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionScreen
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.TransactionScreen
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
import com.sgcdeveloper.moneymanager.presentation.ui.transactionCategoryStatistic.TransactionCategoryStatisticScreen
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
                val it = TimeInternalSingleton.timeIntervalController!!
                val timeInterval = when(it){
                    is TimeIntervalController.DailyController ->{
                        TimeIntervalController.DailyController(it.date)
                    }
                    is TimeIntervalController.WeeklyController ->{
                        TimeIntervalController.WeeklyController(it.startDay,it.endDay)
                    }
                    is TimeIntervalController.MonthlyController ->{
                        TimeIntervalController.MonthlyController(it.date)
                    }
                    is TimeIntervalController.QuarterlyController ->{
                        TimeIntervalController.QuarterlyController(it.startDay,it.endDay)
                    }
                    is TimeIntervalController.YearlyController ->{
                        TimeIntervalController.QuarterlyController(it.date)
                    }
                    is TimeIntervalController.AllController ->{
                        TimeIntervalController.AllController(it.allString)
                    }
                }
                timeIntervalTransactionsViewModel.onEvent(
                    TimeIntervalTransactionEvent.ChangeTimeInterval(
                        timeInterval
                    )
                )
                TimeInternalSingleton.timeIntervalController = null
            }
            timeIntervalTransactionsViewModel.onEvent(TimeIntervalTransactionEvent.ChangeTransactionCategoryFilter(TransactionCategory.All))

            TimeIntervalTransactionsScreen(timeIntervalTransactionsViewModel, navController)
        }

        composable(Screen.TransactionCategoryStatisticScreen(null).route + "{screen}") { backStackEntry ->
            TransactionCategoryStatisticScreen(
                statisticViewModel,
                navController,
                Gson().fromJson(backStackEntry.arguments?.getString("screen"), TransactionScreen::class.java)
            )
        }

        composable("TransactionCategoryTransactions/" + "{category}" + "/" + "{wallet}") { backStackEntry ->
            val category =
                Gson().fromJson(backStackEntry.arguments?.getString("category"), TransactionCategory::class.java)

            val walletJson = backStackEntry.arguments?.getString("wallet")
            val wallet = Gson().fromJson(walletJson, Wallet::class.java)
            timeIntervalTransactionsViewModel.onEvent(TimeIntervalTransactionEvent.SetDefaultWallet(wallet))

            if (TimeInternalSingleton.timeIntervalController != null) {
                val it = TimeInternalSingleton.timeIntervalController!!
                val timeInterval = when(it){
                    is TimeIntervalController.DailyController ->{
                        TimeIntervalController.DailyController(it.date)
                    }
                    is TimeIntervalController.WeeklyController ->{
                        TimeIntervalController.WeeklyController(it.startDay,it.endDay)
                    }
                    is TimeIntervalController.MonthlyController ->{
                        TimeIntervalController.MonthlyController(it.date)
                    }
                    is TimeIntervalController.QuarterlyController ->{
                        TimeIntervalController.QuarterlyController(it.startDay,it.endDay)
                    }
                    is TimeIntervalController.YearlyController ->{
                        TimeIntervalController.QuarterlyController(it.date)
                    }
                    is TimeIntervalController.AllController ->{
                        TimeIntervalController.AllController(it.allString)
                    }
                }
                timeIntervalTransactionsViewModel.onEvent(
                    TimeIntervalTransactionEvent.ChangeTimeInterval(
                        timeInterval
                    )
                )
                TimeInternalSingleton.timeIntervalController = null
            }
            timeIntervalTransactionsViewModel.onEvent(TimeIntervalTransactionEvent.ChangeTransactionCategoryFilter(category))
            TimeIntervalTransactionsScreen(
                timeIntervalTransactionsViewModel,
                navController
            )
        }
    }
}