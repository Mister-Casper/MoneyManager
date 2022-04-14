package com.sgcdeveloper.moneymanager.presentation.main

import android.content.Intent
import android.os.Bundle
import android.os.CancellationSignal
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kobakei.ratethisapp.RateThisApp
import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.prefa.DefaultSettings
import com.sgcdeveloper.moneymanager.data.prefa.LoginStatus
import com.sgcdeveloper.moneymanager.domain.model.*
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.util.BudgetPeriod
import com.sgcdeveloper.moneymanager.domain.util.TransactionType
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.MoneyManagerTheme
import com.sgcdeveloper.moneymanager.presentation.theme.black
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import com.sgcdeveloper.moneymanager.presentation.ui.addBudget.AddBudgetEvent
import com.sgcdeveloper.moneymanager.presentation.ui.addBudget.AddBudgetScreen
import com.sgcdeveloper.moneymanager.presentation.ui.addBudget.AddBudgetViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionEvent
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionScreen
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.TransactionScreen
import com.sgcdeveloper.moneymanager.presentation.ui.addWallet.AddWalletScreen
import com.sgcdeveloper.moneymanager.presentation.ui.addWallet.AddWalletViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.addWallet.WalletEvent
import com.sgcdeveloper.moneymanager.presentation.ui.budget.BudgetScreen
import com.sgcdeveloper.moneymanager.presentation.ui.budgetManager.BudgetManagerScreen
import com.sgcdeveloper.moneymanager.presentation.ui.budgetManager.TimeIntervalBudgetManager
import com.sgcdeveloper.moneymanager.presentation.ui.budgetManager.TimeIntervalBudgetManagerViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.homeScreen.HomeViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitScreen
import com.sgcdeveloper.moneymanager.presentation.ui.init.WelcomeScreen
import com.sgcdeveloper.moneymanager.presentation.ui.moneyManagerScreen.MoneyManagerScreen
import com.sgcdeveloper.moneymanager.presentation.ui.registration.RegistrationViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.registration.SignInScreen
import com.sgcdeveloper.moneymanager.presentation.ui.registration.SignUpScreen
import com.sgcdeveloper.moneymanager.presentation.ui.settings.*
import com.sgcdeveloper.moneymanager.presentation.ui.statistic.StatisticEvent
import com.sgcdeveloper.moneymanager.presentation.ui.statistic.StatisticViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions.TimeIntervalTransactionEvent
import com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions.TimeIntervalTransactionsScreen
import com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions.TimeIntervalTransactionsViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.transactionCategoryStatistic.TransactionCategoryStatisticScreen
import com.sgcdeveloper.moneymanager.presentation.ui.util.MyEnterPinActivity
import com.sgcdeveloper.moneymanager.presentation.ui.walletScreen.ShowWalletEvent
import com.sgcdeveloper.moneymanager.presentation.ui.walletScreen.WalletScreen
import com.sgcdeveloper.moneymanager.presentation.ui.walletScreen.WalletViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.walletsManager.WalletsManagerScreen
import com.sgcdeveloper.moneymanager.presentation.ui.weeklyStatisticScreen.WeeklyStatisticScreen
import com.sgcdeveloper.moneymanager.presentation.ui.weeklyStatisticScreen.WeeklyStatisticScreenEvent
import com.sgcdeveloper.moneymanager.presentation.ui.weeklyStatisticScreen.WeeklyStatisticViewModel
import com.sgcdeveloper.moneymanager.util.Date
import com.sgcdeveloper.moneymanager.util.SyncHelper
import com.sgcdeveloper.moneymanager.util.TimeInternalSingleton
import com.sgcdeveloper.moneymanager.util.gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private lateinit var authResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var googleInSigned: (isNewUser: Boolean, userName: String) -> Unit
    private lateinit var googleInFailed: () -> Unit

    @Inject
    lateinit var syncHelper: SyncHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        val pref = AppPreferencesHelper(this, DefaultSettings())
        if (pref.getUserPassword() && savedInstanceState == null) {
            val intent = MyEnterPinActivity.getIntent(this, false)
            startActivityForResult(intent, REQUEST_CODE)
        }
        if (pref.getIsOld()) {
            pref.setLoginStatus(LoginStatus.None)
            pref.setIsOld(false)
        }

        initGoogleAuthListener()
        super.onCreate(savedInstanceState)
        syncHelper.syncLocalData()
        FirebaseApp.initializeApp(this)
        setContent {
            val darkThemeViewModel: MainViewModel = hiltViewModel()

            val navController = rememberAnimatedNavController()

            val systemUiController = rememberSystemUiController()
            if (darkThemeViewModel.isDarkTheme.value) {
                systemUiController.setSystemBarsColor(
                    color = black
                )
            } else {
                systemUiController.setSystemBarsColor(
                    color = blue
                )
            }

            MoneyManagerTheme(darkThemeViewModel.isDarkTheme.value) {
                if (pref.getLoginStatus() == LoginStatus.None && savedInstanceState == null) {
                    val config =
                        RateThisApp.Config(3, 5)
                    config.setMessage(com.sgcdeveloper.moneymanager.R.string.get_feedback)
                    RateThisApp.init(config)
                    RateThisApp.onCreate(this)
                    RateThisApp.showRateDialogIfNeeded(this)
                }
                Surface(
                    color = MaterialTheme.colors.background,
                ) {
                    val registrationViewModel: RegistrationViewModel by viewModels()
                    AnimatedNavHost(navController = navController, startDestination = Screen.SignIn.route) {
                        composable(
                            Screen.SignIn.route,
                            enterTransition = {
                                if (targetState.destination.route == Screen.Init.route) {
                                    slideInVertically(
                                        initialOffsetY = { -it },
                                        animationSpec = tween(100)
                                    )
                                } else slideInHorizontally(
                                    initialOffsetX = { -it },
                                    animationSpec = tween(100)
                                )
                            },
                            exitTransition = {
                                if (targetState.destination.route == Screen.Init.route) {
                                    slideOutVertically(
                                        targetOffsetY = { -it },
                                        animationSpec = tween(100)
                                    )
                                } else slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(100)
                                )
                            }) {
                            SignInScreen(registrationViewModel)
                        }
                        composable(
                            Screen.SignUp.route,
                            enterTransition = {
                                if (targetState.destination.route == Screen.Init.route) {
                                    slideInVertically(
                                        initialOffsetY = { -it },
                                        animationSpec = tween(100)
                                    )
                                }
                                slideInHorizontally(
                                    initialOffsetX = { it },
                                    animationSpec = tween(100)
                                )
                            },
                            exitTransition = {
                                if (targetState.destination.route == Screen.Init.route) {
                                    slideOutVertically(
                                        targetOffsetY = { -it },
                                        animationSpec = tween(100)
                                    )
                                } else slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(100)
                                )
                            }) {
                            SignUpScreen(navController = navController, registrationViewModel)
                        }
                        composable(Screen.Init.route, enterTransition = {
                            slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(100)
                            )
                        }, exitTransition = {
                            slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(100)
                            )
                        }) {
                            InitScreen(hiltViewModel(), navController)
                        }
                        composable(Screen.MoneyManagerScreen.route) {
                            MoneyManagerScreen(navController, hiltViewModel())
                        }
                        composable(Screen.Welcome.route) {
                            WelcomeScreen {
                                navController.navigate(Screen.MoneyManagerScreen.route)
                            }
                        }
                        composable(Screen.AccountSettings.route) {
                            AccountSettings(navController, hiltViewModel(), hiltViewModel())
                        }
                        composable(Screen.AddTransaction(null).route + "{wallet}") { backStackEntry ->
                            val addTransactionViewModel = hiltViewModel<AddTransactionViewModel>()

                            val wallet =
                                gson.fromJson(backStackEntry.arguments?.getString("wallet"), Wallet::class.java)

                            if (wallet != null) {
                                addTransactionViewModel.onEvent(AddTransactionEvent.SetDefaultWallet(wallet))
                                addTransactionViewModel.isMustBeRecurring = false
                                addTransactionViewModel.recurringInterval.value = RecurringInterval.None
                            }
                            AddTransactionScreen(addTransactionViewModel, navController)

                            backStackEntry.arguments?.putString("wallet", "")
                        }
                        composable(Screen.AddRecurringTransaction(null).route + "{transaction}") {
                            val addTransactionViewModel: AddTransactionViewModel = hiltViewModel()

                            val recurringTransaction =
                                gson.fromJson(
                                    it.arguments?.getString("transaction"),
                                    RecurringTransaction::class.java
                                )

                            if (recurringTransaction != null) {
                                addTransactionViewModel.onEvent(
                                    AddTransactionEvent.SetDefaultWRecurringTransaction(
                                        recurringTransaction
                                    )
                                )
                            }
                            AddTransactionScreen(addTransactionViewModel, navController)

                            it.arguments?.putString("transaction", "")
                        }
                        composable("AddRecurringTransaction/") {
                            val addTransactionViewModel: AddTransactionViewModel = hiltViewModel()
                            addTransactionViewModel.isMustBeRecurring = true
                            addTransactionViewModel.recurringInterval.value = RecurringInterval.Daily(
                                _lastTransactionDate = null,
                                _isForever = true,
                                _endDate = Date(LocalDateTime.now()),
                                _repeatIInterval = 1,
                                times = 1,
                                type = RecurringEndType.Forever
                            )
                            AddTransactionScreen(addTransactionViewModel, navController)
                        }
                        composable("EditTransaction/{transaction}") { backStackEntry ->
                            val addTransactionViewModel: AddTransactionViewModel = hiltViewModel()

                            val transaction =
                                gson.fromJson(
                                    backStackEntry.arguments?.getString("transaction"),
                                    Transaction::class.java
                                )

                            if (transaction != null)
                                addTransactionViewModel.onEvent(AddTransactionEvent.SetExistTransaction(transaction))
                            AddTransactionScreen(addTransactionViewModel, navController)

                            backStackEntry.arguments?.putString("transaction", "")
                        }
                        composable(Screen.AddWallet(null).route + "{wallet}") { backStackEntry ->
                            val addWalletViewModel: AddWalletViewModel = hiltViewModel()

                            val wallet =
                                gson.fromJson(backStackEntry.arguments?.getString("wallet"), Wallet::class.java)
                            if (wallet != null)
                                addWalletViewModel.onEvent(WalletEvent.SetWallet(wallet))
                            AddWalletScreen(navController, addWalletViewModel)

                            backStackEntry.arguments?.putString("wallet", "")
                        }
                        composable(Screen.TimeIntervalTransactions(null).route + "{wallet}") { backStackEntry ->
                            val timeIntervalTransactionsViewModel: TimeIntervalTransactionsViewModel = hiltViewModel()

                            val walletJson = backStackEntry.arguments?.getString("wallet")
                            val wallet = gson.fromJson(walletJson, Wallet::class.java)
                            if (wallet != null)
                                timeIntervalTransactionsViewModel.onEvent(
                                    TimeIntervalTransactionEvent.SetDefaultWallet(
                                        wallet
                                    )
                                )
                            if (TimeInternalSingleton.timeIntervalController != null) {
                                val it = TimeInternalSingleton.timeIntervalController!!
                                val timeInterval = when (it) {
                                    is TimeIntervalController.DailyController -> {
                                        TimeIntervalController.DailyController(it.date)
                                    }
                                    is TimeIntervalController.WeeklyController -> {
                                        TimeIntervalController.WeeklyController(it.startDay, it.endDay)
                                    }
                                    is TimeIntervalController.MonthlyController -> {
                                        TimeIntervalController.MonthlyController(it.date)
                                    }
                                    is TimeIntervalController.QuarterlyController -> {
                                        TimeIntervalController.QuarterlyController(it.startDay, it.endDay)
                                    }
                                    is TimeIntervalController.YearlyController -> {
                                        TimeIntervalController.QuarterlyController(it.date)
                                    }
                                    is TimeIntervalController.AllController -> {
                                        TimeIntervalController.AllController(it.allString)
                                    }
                                    is TimeIntervalController.CustomController -> {
                                        val controller = TimeIntervalController.CustomController()
                                        controller.startIntervalDate = it.startIntervalDate
                                        controller.endIntervalDate = it.endIntervalDate
                                        controller
                                    }
                                }
                                timeIntervalTransactionsViewModel.onEvent(
                                    TimeIntervalTransactionEvent.ChangeTimeInterval(
                                        timeInterval
                                    )
                                )
                                TimeInternalSingleton.timeIntervalController = null
                            }
                            timeIntervalTransactionsViewModel.onEvent(
                                TimeIntervalTransactionEvent.ChangeTransactionCategoryFilter(
                                    All(LocalContext.current)
                                )
                            )

                            TimeIntervalTransactionsScreen(timeIntervalTransactionsViewModel, navController)

                            backStackEntry.arguments?.putString("wallet", "")
                        }
                        composable(Screen.TransactionCategoryStatisticScreen(null).route + "{screen}") { backStackEntry ->
                            TransactionCategoryStatisticScreen(
                                hiltViewModel(),
                                navController,
                                gson.fromJson(
                                    backStackEntry.arguments?.getString("screen"),
                                    TransactionScreen::class.java
                                )
                            )
                        }
                        composable(Screen.TransactionCategoryForWalletStatisticScreen(null).route + "{wallet}") { backStackEntry ->
                            val statisticViewModel: StatisticViewModel = hiltViewModel()
                            val walletJson = backStackEntry.arguments?.getString("wallet")
                            val wallet = gson.fromJson(walletJson, Wallet::class.java)
                            if (wallet != null)
                                statisticViewModel.onEvent(StatisticEvent.SetWallet(wallet))
                            TransactionCategoryStatisticScreen(
                                statisticViewModel,
                                navController,
                                TransactionScreen.Expense
                            )

                            backStackEntry.arguments?.putString("wallet", "")
                        }
                        composable("TransactionCategoryTransactions/" + "{category}" + "/" + "{wallet}") { backStackEntry ->
                            val timeIntervalTransactionsViewModel: TimeIntervalTransactionsViewModel = hiltViewModel()

                            val category =
                                gson.fromJson(
                                    backStackEntry.arguments?.getString("category"),
                                    TransactionCategory::class.java
                                )

                            val walletJson = backStackEntry.arguments?.getString("wallet")
                            val wallet = gson.fromJson(walletJson, Wallet::class.java)
                            if (wallet != null)
                                timeIntervalTransactionsViewModel.onEvent(
                                    TimeIntervalTransactionEvent.SetDefaultWallet(
                                        wallet
                                    )
                                )

                            if (TimeInternalSingleton.timeIntervalController != null) {
                                val it = TimeInternalSingleton.timeIntervalController!!
                                val timeInterval = when (it) {
                                    is TimeIntervalController.DailyController -> {
                                        TimeIntervalController.DailyController(it.date)
                                    }
                                    is TimeIntervalController.WeeklyController -> {
                                        TimeIntervalController.WeeklyController(it.startDay, it.endDay)
                                    }
                                    is TimeIntervalController.MonthlyController -> {
                                        TimeIntervalController.MonthlyController(it.date)
                                    }
                                    is TimeIntervalController.QuarterlyController -> {
                                        TimeIntervalController.QuarterlyController(it.startDay, it.endDay)
                                    }
                                    is TimeIntervalController.YearlyController -> {
                                        TimeIntervalController.YearlyController(it.date)
                                    }
                                    is TimeIntervalController.AllController -> {
                                        TimeIntervalController.AllController(it.allString)
                                    }
                                    is TimeIntervalController.CustomController -> {
                                        val controller = TimeIntervalController.CustomController()
                                        controller.startIntervalDate = it.startIntervalDate
                                        controller.endIntervalDate = it.endIntervalDate
                                        controller
                                    }
                                }
                                timeIntervalTransactionsViewModel.onEvent(
                                    TimeIntervalTransactionEvent.ChangeTimeInterval(
                                        timeInterval
                                    )
                                )
                                TimeInternalSingleton.timeIntervalController = null
                            }
                            if (category != null)
                                timeIntervalTransactionsViewModel.onEvent(
                                    TimeIntervalTransactionEvent.ChangeTransactionCategoryFilter(
                                        category
                                    )
                                )
                            TimeIntervalTransactionsScreen(
                                timeIntervalTransactionsViewModel,
                                navController
                            )
                            backStackEntry.arguments?.putString("wallet", "")
                            backStackEntry.arguments?.putString("category", "")
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(navController, darkThemeViewModel)
                        }
                        composable(Screen.Calculators.route) {
                            CalculatorsScreen(navController, hiltViewModel())
                        }
                        composable(Screen.Calculator.route) {
                            CalculatorScreen(navController, hiltViewModel())
                        }
                        composable(Screen.TipCalculator.route) {
                            TipCalculatorScreen(navController, darkThemeViewModel)
                        }
                        composable(Screen.DepositCalculator.route) {
                            DepositCalculatorScreen(navController, darkThemeViewModel)
                        }
                        composable(Screen.WalletScreen(null).route + "{wallet}") { backStackEntry ->
                            val walletViewModel: WalletViewModel = hiltViewModel()
                            val walletJson = backStackEntry.arguments?.getString("wallet")
                            val wallet = gson.fromJson(walletJson, Wallet::class.java)
                            if (wallet != null)
                                walletViewModel.onEvent(ShowWalletEvent.SetShowWallet(wallet))
                            WalletScreen(walletViewModel, navController)
                            backStackEntry.arguments?.putString("wallet", "")
                        }
                        composable(Screen.PasswordSettings.route) {
                            PasswordSettings(navController, hiltViewModel())
                        }
                        composable("WeeklyStatisticScreen/" + "{wallet}" + "/" + "{type}") { backStackEntry ->
                            val weeklyStatisticViewModel: WeeklyStatisticViewModel = hiltViewModel()

                            val walletJson = backStackEntry.arguments?.getString("wallet")
                            val wallet = gson.fromJson(walletJson, Wallet::class.java)
                            val type = gson.fromJson(
                                backStackEntry.arguments?.getString("type"),
                                TransactionType::class.java
                            )
                            if (wallet != null) {
                                weeklyStatisticViewModel.onEvent(
                                    WeeklyStatisticScreenEvent.Init(wallet, type)
                                )
                            }
                            WeeklyStatisticScreen(navController, weeklyStatisticViewModel)
                            backStackEntry.arguments?.putString("wallet", "")
                        }
                        composable(Screen.WalletsManagerScreen.route) {
                            WalletsManagerScreen(hiltViewModel(), navController)
                        }
                        composable(Screen.ExchangeRatesScreen.route) {
                            ExchangeRatesScreen(navController, hiltViewModel())
                        }
                        composable(Screen.AddBudgetScreen(null).route + "{budget}") {
                            val addBudgetViewModel: AddBudgetViewModel = hiltViewModel()
                            val budget = gson.fromJson(it.arguments?.getString("budget"), BudgetEntry::class.java)
                            if (budget != null)
                                addBudgetViewModel.onEvent(AddBudgetEvent.SetDefaultBudget(budget))
                            AddBudgetScreen(addBudgetViewModel, navController)
                            it.arguments?.putString("budget", "")
                        }
                        composable("AddBudgetScreen/") {
                            AddBudgetScreen(hiltViewModel(), navController)
                        }
                        composable(Screen.BudgetScreen(null).route + "{budget}") {
                            val budgetJson = it.arguments?.getString("budget")
                            val budget = gson.fromJson(budgetJson, BaseBudget.BudgetItem::class.java)
                            BudgetScreen(hiltViewModel(), budget, navController)
                        }
                        composable(Screen.BudgetManagerScreen.route) {
                            val homeViewModel: HomeViewModel = hiltViewModel()
                            BudgetManagerScreen(homeViewModel, navController)
                        }
                        composable(Screen.TimeIntervalBudgetManager(null).route + "{period}") {
                            val timeIntervalBudgetManagerViewModel: TimeIntervalBudgetManagerViewModel = hiltViewModel()
                            val period = gson.fromJson(it.arguments?.getString("period"), BudgetPeriod::class.java)
                            if (period != null)
                                timeIntervalBudgetManagerViewModel.loadBudgets(period)
                            TimeIntervalBudgetManager(navController, timeIntervalBudgetManagerViewModel)
                            it.arguments?.putString("period", "")
                        }
                        composable("TransactionCategoriesSettings/{isIncome}") {
                            val vm: TransactionCategoriesSettingsViewModel = hiltViewModel()
                            LaunchedEffect(Unit) {
                                val isIncome = it.arguments?.getString("isIncome", true.toString())!!
                                vm.isShowIncomeCategories.value = isIncome.toBoolean()
                            }
                            TransactionCategoriesSettings(navController, vm)
                        }
                    }

                    if (registrationViewModel.navigationRoute.value.isNotEmpty()) {
                        navController.navigate(registrationViewModel.navigationRoute.value)
                    }

                    registrationViewModel.onGoogleSignIn.observe(this) {
                        it?.getContentIfNotHandled()?.let {
                            googleInSigned = it.second
                            googleInFailed = it.third
                            authResultLauncher.launch(it.first)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val REQUEST_CODE = 123
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE -> if (resultCode == MyEnterPinActivity.RESULT_BACK_PRESSED) {
                finishAffinity()
            } else {
                val cancellationSignal = CancellationSignal()
                cancellationSignal.cancel();
            }
        }
    }

    private fun initGoogleAuthListener() {
        authResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            googleInSigned(task.result.additionalUserInfo!!.isNewUser, account.displayName!!)
                        } else {
                            googleInFailed()
                        }
                    }.addOnCanceledListener(googleInFailed)
                    .addOnFailureListener { googleInFailed() }

            } catch (e: ApiException) {
            }
        }
    }

    override fun onPause() {
        super.onPause()
        GlobalScope.launch {
            syncHelper.syncServerData()
        }
    }
}