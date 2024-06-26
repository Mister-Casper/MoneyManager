package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sgcdeveloper.moneymanager.presentation.theme.wallet_colors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColorPicker(size: Dp, selected: Int = 0, nSelected: (item: Int) -> Unit) {
    val selectedItem = remember { mutableStateOf(selected) }

    LazyVerticalGrid(
        cells = GridCells.Adaptive(size),
        userScrollEnabled = false,
        // Костыль чтобы не конфликтовали LazyColumn и LazyVerticalGrid
        modifier = Modifier.heightIn(0.dp, 30000.dp)
    ) {
        items(wallet_colors.size) {
            val color = wallet_colors[it]
            Box(modifier = Modifier.padding(2.dp)) {
                if (wallet_colors[it].toArgb() == selectedItem.value) {
                    Box(
                        modifier = Modifier
                            .background(color)
                            .size(size)
                            .border(BorderStroke(2.dp, MaterialTheme.colors.onBackground))
                    )
                } else
                    Box(
                        modifier = Modifier
                            .background(color)
                            .size(size)
                            .clickable {
                                nSelected(wallet_colors[it].toArgb())
                                selectedItem.value = wallet_colors[it].toArgb()
                            }
                    )
            }
        }
    }
}