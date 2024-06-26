package com.sgcdeveloper.moneymanager.presentation.ui.walletsManager

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.Wallet
import com.sgcdeveloper.moneymanager.presentation.nav.Screen
import com.sgcdeveloper.moneymanager.presentation.theme.gray
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.composables.AutoSizeText
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.DeleteWalletDialog
import com.sgcdeveloper.moneymanager.presentation.ui.dialogs.InformationDialog
import com.sgcdeveloper.moneymanager.presentation.ui.homeScreen.HomeViewModel
import org.burnoutcrew.reorderable.*

@Composable
fun WalletsManagerScreen(
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    val wallets = remember { homeViewModel.state }.value.existWallets
    var showDeleteWalletDialog by remember { mutableStateOf<Wallet?>(null) }

    if (showDeleteWalletDialog != null) {
        if (showDeleteWalletDialog!!.isDefault) {
            InformationDialog(stringResource(R.string.cant_delete_default_wallet)) {
                showDeleteWalletDialog = null
            }
        } else {
            DeleteWalletDialog(showDeleteWalletDialog, {
                homeViewModel.deleteWallet(showDeleteWalletDialog!!)
                showDeleteWalletDialog = null
            }, {
                showDeleteWalletDialog = null
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            Row(
                Modifier
                    .align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew,
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(32.dp)
                        .clickable {
                            homeViewModel.save()
                            navController.popBackStack()
                        }
                )
                Text(
                    text = stringResource(id = R.string.manage_wallets),
                    fontSize = 22.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 12.dp),
                    color = MaterialTheme.colors.onBackground
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.add_icon),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .size(32.dp)
                    .clickable {
                        navController.navigate(Screen.AddWallet(homeViewModel.state.value.wallets.last()).route)
                    }
            )
        }
        VerticalReorderList(
            wallets,
            onMove = { from, to -> homeViewModel.move(from, to) },
            canDragOver = { true },
            navController = navController, onDelete = {
                showDeleteWalletDialog = it
            }
        )
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                AdView(context).apply {
                    adSize = AdSize.LARGE_BANNER
                    adUnitId = "ca-app-pub-5494709043617393/2510789678"
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }

    BackHandler {
        homeViewModel.save()
        navController.popBackStack()
    }
}

@Composable
private fun VerticalReorderList(
    items: List<Wallet>,
    state: ReorderableState = rememberReorderState(),
    onMove: (fromPos: ItemPosition, toPos: ItemPosition) -> (Unit),
    canDragOver: ((pos: ItemPosition) -> Boolean),
    navController: NavController,
    onDelete: (wallet: Wallet) -> Unit
) {
    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
            .then(Modifier.reorderable(state, onMove = onMove, canDragOver = canDragOver))
    ) {
        items(items, { it.walletId }) { item ->
            Row(
                Modifier
                    .border(BorderStroke(1.dp, gray))
                    .padding(6.dp)
                    .draggedItem(state.offsetByKey(item.walletId))
                    .clickable {

                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .size(54.dp)
                        .padding(4.dp)
                        .align(Alignment.CenterVertically),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Box(modifier = Modifier.background(Color(item.color))) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = "",
                            Modifier
                                .align(Alignment.Center)
                                .size(40.dp),
                            tint = white
                        )
                    }
                }
                Column(
                    Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                ) {
                    AutoSizeText(
                        text = item.name,
                        suggestedFontSizes = listOf(16.sp, 14.sp, 12.sp)
                    )

                    AutoSizeText(
                        text = item.formattedMoney,
                        suggestedFontSizes = listOf(14.sp, 12.sp, 10.sp),
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.edit_icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 4.dp)
                        .clickable {
                            navController.navigate(Screen.AddWallet(item).route)
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.delete_icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 4.dp)
                        .clickable {
                            onDelete(item)
                        }
                )
                Icon(
                    painter = painterResource(id = R.drawable.list_icon),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 4.dp)
                        .detectReorder(state)
                )
            }
        }
    }
}