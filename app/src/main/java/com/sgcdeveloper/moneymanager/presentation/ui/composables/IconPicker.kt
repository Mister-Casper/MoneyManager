package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconPicker(icons:List<Int>,size: Dp, selected: Int = 0, nSelected: (item: Int) -> Unit) {
    val selectedItem = remember { mutableStateOf(selected) }

    LazyVerticalGrid(
        cells = GridCells.Adaptive(size),
        userScrollEnabled = false,
        // Костыль чтобы не конфликтовали LazyColumn и LazyVerticalGrid
        modifier = Modifier.heightIn(0.dp, 30000.dp)
    ) {
        items(icons.size) {
            val icon = icons[it]
            Box(modifier = Modifier.padding(2.dp)) {
                if (selectedItem.value == icons[it]) {
                    Box(
                        modifier = Modifier
                            .size(size)
                            .border(BorderStroke(2.dp, MaterialTheme.colors.onBackground))
                    ) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "",
                            modifier = Modifier.size(size)
                        )
                    }
                } else
                    Box(
                        modifier = Modifier
                            .size(size)
                            .clickable {
                                nSelected(it)
                                selectedItem.value = icons[it]
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "",
                            modifier = Modifier.size(size)
                        )
                    }
            }
        }
    }
}