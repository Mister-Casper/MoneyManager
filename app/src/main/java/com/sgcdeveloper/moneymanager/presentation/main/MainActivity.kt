package com.sgcdeveloper.moneymanager.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.domain.timeInterval.TimeIntervalController
import com.sgcdeveloper.moneymanager.domain.util.TransactionCategory
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.MoneyManagerTheme
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionEvent
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionScreen
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.AddTransactionViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.addTransactionScreen.TransactionScreen
import com.sgcdeveloper.moneymanager.presentation.ui.addWallet.AddWalletScreen
import com.sgcdeveloper.moneymanager.presentation.ui.addWallet.AddWalletViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.addWallet.WalletEvent
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitScreen
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.moneyManagerScreen.MoneyManagerScreen
import com.sgcdeveloper.moneymanager.presentation.ui.registration.RegistrationViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.registration.SignInScreen
import com.sgcdeveloper.moneymanager.presentation.ui.registration.SignUpScreen
import com.sgcdeveloper.moneymanager.presentation.ui.settings.AccountSettings
import com.sgcdeveloper.moneymanager.presentation.ui.settings.AccountSettingsViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.settings.SettingsScreen
import com.sgcdeveloper.moneymanager.presentation.ui.statistic.StatisticEvent
import com.sgcdeveloper.moneymanager.presentation.ui.statistic.StatisticViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions.TimeIntervalTransactionEvent
import com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions.TimeIntervalTransactionsScreen
import com.sgcdeveloper.moneymanager.presentation.ui.timeIntervalTransactions.TimeIntervalTransactionsViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.transactionCategoryStatistic.TransactionCategoryStatisticScreen
import com.sgcdeveloper.moneymanager.presentation.ui.walletScreen.ShowWalletEvent
import com.sgcdeveloper.moneymanager.presentation.ui.walletScreen.WalletScreen
import com.sgcdeveloper.moneymanager.presentation.ui.walletScreen.WalletViewModel
import com.sgcdeveloper.moneymanager.util.SyncHelper
import com.sgcdeveloper.moneymanager.util.TimeInternalSingleton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var authResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var googleInSigned: (isNewUser: Boolean, userName: String) -> Unit
    private lateinit var googleInFailed: () -> Unit

    @Inject
    lateinit var syncHelper: SyncHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        initGoogleAuthListener()
        super.onCreate(savedInstanceState)
        syncHelper.syncLocalData()
        FirebaseApp.initializeApp(this)
        setContent {
            val navController = rememberAnimatedNavController()
            val darkThemeViewModel: DarkThemeViewModel by viewModels()

            MoneyManagerTheme(darkThemeViewModel.isDarkTheme.value) {
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.padding(top = LocalContext.current.pxToDp(getStatusBarHeight()).dp)
                ) {
                    AnimatedNavHost(navController = navController, startDestination = Screen.SignIn.route) {
                        val registrationViewModel: RegistrationViewModel by viewModels()

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
                            SignInScreen(registrationViewModel = registrationViewModel)
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
                            SignUpScreen(navController = navController, registrationViewModel = registrationViewModel)
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
                            val initViewModel: InitViewModel by viewModels()
                            InitScreen(initViewModel, navController)
                        }
                        composable(Screen.MoneyManagerScreen.route) {
                            MoneyManagerScreen(navController)
                        }
                        composable(Screen.AccountSettings.route) {
                            val accountSettingsViewModel: AccountSettingsViewModel by viewModels()
                            AccountSettings(navController, accountSettingsViewModel)
                        }
                        composable(Screen.AddTransaction(null).route + "{wallet}") { backStackEntry ->
                            val addTransactionViewModel: AddTransactionViewModel by viewModels()

                            val wallet =
                                Gson().fromJson(backStackEntry.arguments?.getString("wallet"), Wallet::class.java)

                            addTransactionViewModel.onEvent(AddTransactionEvent.SetDefaultWallet(wallet))
                            AddTransactionScreen(addTransactionViewModel, navController)
                        }
                        composable(Screen.EditTransaction(null).route + "{transaction}") { backStackEntry ->
                            val addTransactionViewModel: AddTransactionViewModel by (LocalContext.current as MainActivity).viewModels()

                            val transaction =
                                Gson().fromJson(
                                    backStackEntry.arguments?.getString("transaction"),
                                    TransactionEntry::class.java
                                )

                            if (transaction != null)
                                addTransactionViewModel.onEvent(AddTransactionEvent.SetExistTransaction(transaction))
                            AddTransactionScreen(addTransactionViewModel, navController)

                            backStackEntry.arguments?.putString("transaction", "")
                        }
                        composable(Screen.AddWallet(null).route + "{wallet}") { backStackEntry ->
                            val addWalletViewModel: AddWalletViewModel by viewModels()

                            val wallet =
                                Gson().fromJson(backStackEntry.arguments?.getString("wallet"), Wallet::class.java)
                            if (wallet != null)
                                addWalletViewModel.onEvent(WalletEvent.SetWallet(wallet))
                            AddWalletScreen(navController, addWalletViewModel)

                            backStackEntry.arguments?.putString("wallet", "")
                        }
                        composable(Screen.TimeIntervalTransactions(null).route + "{wallet}") { backStackEntry ->
                            val timeIntervalTransactionsViewModel: TimeIntervalTransactionsViewModel by viewModels()

                            val walletJson = backStackEntry.arguments?.getString("wallet")
                            val wallet = Gson().fromJson(walletJson, Wallet::class.java)
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
                                    TransactionCategory.All
                                )
                            )

                            TimeIntervalTransactionsScreen(timeIntervalTransactionsViewModel, navController)
                        }
                        composable(Screen.TransactionCategoryStatisticScreen(null).route + "{screen}") { backStackEntry ->
                            val statisticViewModel: StatisticViewModel by viewModels()
                            TransactionCategoryStatisticScreen(
                                statisticViewModel,
                                navController,
                                Gson().fromJson(
                                    backStackEntry.arguments?.getString("screen"),
                                    TransactionScreen::class.java
                                )
                            )
                        }
                        composable(Screen.TransactionCategoryForWalletStatisticScreen(null).route + "{wallet}") { backStackEntry ->
                            val statisticViewModel: StatisticViewModel by viewModels()
                            val walletJson = backStackEntry.arguments?.getString("wallet")
                            val wallet = Gson().fromJson(walletJson, Wallet::class.java)
                            statisticViewModel.onEvent(StatisticEvent.SetWallet(wallet))
                            TransactionCategoryStatisticScreen(
                                statisticViewModel,
                                navController,
                                TransactionScreen.Expense
                            )
                        }
                        composable("TransactionCategoryTransactions/" + "{category}" + "/" + "{wallet}") { backStackEntry ->
                            val timeIntervalTransactionsViewModel: TimeIntervalTransactionsViewModel by viewModels()

                            val category =
                                Gson().fromJson(
                                    backStackEntry.arguments?.getString("category"),
                                    TransactionCategory::class.java
                                )

                            val walletJson = backStackEntry.arguments?.getString("wallet")
                            val wallet = Gson().fromJson(walletJson, Wallet::class.java)
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
                                    category
                                )
                            )
                            TimeIntervalTransactionsScreen(
                                timeIntervalTransactionsViewModel,
                                navController
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(navController, darkThemeViewModel)
                        }
                        composable(Screen.WalletScreen(null).route + "{wallet}") { backStackEntry ->
                            val walletViewModel: WalletViewModel by viewModels()
                            val walletJson = backStackEntry.arguments?.getString("wallet")
                            val wallet = Gson().fromJson(walletJson, Wallet::class.java)
                            walletViewModel.onEvent(ShowWalletEvent.SetShowWallet(wallet))
                            WalletScreen(walletViewModel, navController)
                        }
                    }

                    val registrationViewModel: RegistrationViewModel by viewModels()
                    if (registrationViewModel.navigationRoute.value.isNotEmpty()) {
                        navController.navigate(registrationViewModel.navigationRoute.value)
                        registrationViewModel.navigationRoute.value = ""
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

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun Context.pxToDp(px: Int): Int {
        return (px / resources.displayMetrics.density).toInt()
    }

}