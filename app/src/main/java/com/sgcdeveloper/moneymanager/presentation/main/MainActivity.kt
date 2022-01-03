package com.sgcdeveloper.moneymanager.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sgcdeveloper.moneymanager.data.prefa.LoginStatus
import com.sgcdeveloper.moneymanager.presentation.theme.MoneyManagerTheme
import com.sgcdeveloper.moneymanager.presentation.ui.homeScreen.HomeViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.moneyManagerScreen.MoneyManagerScreen
import com.sgcdeveloper.moneymanager.presentation.ui.moneyManagerScreen.MoneyManagerViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.registration.InitScreen
import com.sgcdeveloper.moneymanager.presentation.ui.registration.RegistrationViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.registration.StartScreen
import com.sgcdeveloper.moneymanager.presentation.ui.statisticScreen.StatisticViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.transactionScreen.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var authResultLauncher: ActivityResultLauncher<Intent>
    private val isGoogleSigned = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        authResultLauncher = init(this)
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            val homeViewModel: HomeViewModel by viewModels()
            val statisticViewModel: StatisticViewModel by viewModels()
            val transactionViewModel: TransactionViewModel by viewModels()
            val moneyManagerViewModel: MoneyManagerViewModel by viewModels()
            val registrationViewModel: RegistrationViewModel by viewModels()
            MoneyManagerTheme {
                this.window.statusBarColor = MaterialTheme.colors.primaryVariant.toArgb()

                val loginStatus = remember { registrationViewModel.loginStatus }

                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.padding(top = LocalContext.current.pxToDp(getStatusBarHeight()).dp)
                ) {
                    if (loginStatus.value == LoginStatus.None) {
                        MoneyManagerScreen(
                            moneyManagerViewModel,
                            homeViewModel,
                            transactionViewModel,
                            statisticViewModel
                        )
                    } else if (loginStatus.value == LoginStatus.Registering)
                        StartScreen(registrationViewModel, authResultLauncher, isGoogleSigned)
                    else
                        InitScreen()
                }
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

    private fun init(context: Context): ActivityResultLauncher<Intent> {
        return (context as ComponentActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {

            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isGoogleSigned.value = true
                } else {

                }
            }
    }


}