package com.sgcdeveloper.moneymanager.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.ui.graphics.Color
import com.sgcdeveloper.moneymanager.presentation.theme.MoneyManagerTheme
import com.sgcdeveloper.moneymanager.presentation.ui.homeScreen.HomeViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.moneyManagerScreen.MoneyManagerScreen
import com.sgcdeveloper.moneymanager.presentation.ui.moneyManagerScreen.MoneyManagerViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.statisticScreen.StatisticViewModel
import com.sgcdeveloper.moneymanager.presentation.ui.transactionScreen.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val homeViewModel: HomeViewModel by viewModels()
            val statisticViewModel: StatisticViewModel by viewModels()
            val transactionViewModel: TransactionViewModel by viewModels()
            val moneyManagerViewModel: MoneyManagerViewModel by viewModels()
            MoneyManagerTheme {
                Surface(color = MaterialTheme.colors.background) {
                    this.window.statusBarColor = MaterialTheme.colors.secondary.toArgb()
                    MoneyManagerScreen(moneyManagerViewModel, homeViewModel, transactionViewModel, statisticViewModel)
                }
            }
        }
    }
}
