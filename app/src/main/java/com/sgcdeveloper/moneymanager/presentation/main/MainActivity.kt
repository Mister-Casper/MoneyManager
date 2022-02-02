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
import androidx.compose.ui.graphics.toArgb
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
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.MoneyManagerTheme
import com.sgcdeveloper.moneymanager.presentation.ui.homeScreen.HomeViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitScreen
import com.sgcdeveloper.moneymanager.presentation.ui.init.InitViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.moneyManagerScreen.MoneyManagerScreen
import com.sgcdeveloper.moneymanager.presentation.ui.moneyManagerScreen.MoneyManagerViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.registration.RegistrationViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.registration.SignInScreen
import com.sgcdeveloper.moneymanager.presentation.ui.registration.SignUpScreen
import com.sgcdeveloper.moneymanager.presentation.ui.statisticScreen.StatisticViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.transactionScreen.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var authResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var googleInSigned: (isNewUser:Boolean) -> Unit
    private lateinit var googleInFailed: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        initGoogleAuthListener()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            val initViewModel: InitViewModel by viewModels()
            val homeViewModel: HomeViewModel by viewModels()
            val statisticViewModel: StatisticViewModel by viewModels()
            val transactionViewModel: TransactionViewModel by viewModels()
            val moneyManagerViewModel: MoneyManagerViewModel by viewModels()
            val registrationViewModel: RegistrationViewModel by viewModels()
            val navController = rememberAnimatedNavController()

            MoneyManagerTheme {
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.padding(top = LocalContext.current.pxToDp(getStatusBarHeight()).dp)
                ) {
                    this.window.statusBarColor = MaterialTheme.colors.primaryVariant.toArgb()

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
                            InitScreen(initViewModel)
                        }
                        composable(Screen.MoneyManagerScreen.route) {
                            MoneyManagerScreen(
                                moneyManagerViewModel,
                                homeViewModel,
                                transactionViewModel,
                                statisticViewModel
                            )
                        }
                    }

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
                            googleInSigned(task.result.additionalUserInfo!!.isNewUser)
                        }
                    }
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