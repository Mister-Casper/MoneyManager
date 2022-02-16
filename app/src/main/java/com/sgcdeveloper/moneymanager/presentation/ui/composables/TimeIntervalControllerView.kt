package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RowScope.TimeIntervalControllerView (onLeftClick:()->Unit,onRightClick:()->Unit,isEnable:Boolean,description:String){
    RepeatingButton(onClick = onLeftClick, enabled = isEnable ){
        Icon(
            imageVector = Icons.Filled.ArrowLeft,
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(48.dp)
        )
    }
    Text(
        text = description,
        Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        textAlign = TextAlign.Center,
        fontSize = 22.sp,
        color = MaterialTheme.colors.secondary
    )
    RepeatingButton(onClick = onRightClick, enabled = isEnable ){
        Icon(
            imageVector = Icons.Filled.ArrowRight,
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(48.dp)
        )
    }
}