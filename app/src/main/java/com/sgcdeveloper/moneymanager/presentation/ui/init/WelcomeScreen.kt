package com.sgcdeveloper.moneymanager.presentation.ui.init

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.blue
import kotlinx.coroutines.launch

data class WelComePage(val titleId: Int, val iconId: Int, val massageId: Int)

val pages = listOf(
    WelComePage(R.string.welcome_page_title_1, R.mipmap.icon, R.string.welcome_page_massage_1),
    WelComePage(R.string.welcome_page_title_2, R.drawable.welcome_budget_1, R.string.welcome_page_massage_2),
    WelComePage(R.string.welcome_page_title_3, R.drawable.welcome_ico_transactions, R.string.welcome_page_massage_3),
    WelComePage(R.string.welcome_page_title_4, R.drawable.welcome_icon_4, R.string.welcome_page_massage_4),
    WelComePage(R.string.welcome_page_title_5, R.drawable.welcome_icon_3, R.string.welcome_page_massage_5),
    WelComePage(R.string.welcome_page_title_6, R.drawable.welcome_icon_recurring, R.string.welcome_page_massage_6),
    WelComePage(R.string.welcome_page_title_7, R.drawable.welcome_image_2, R.string.welcome_page_massage_7),
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WelcomeScreen(onSkip: () -> Unit) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.skip),
                color = blue,
                modifier = Modifier
                    .padding(end = 12.dp, top = 12.dp)
                    .clickable { onSkip() }
                    .align(Alignment.CenterEnd))
        }
        HorizontalPager(modifier = Modifier.weight(1f), count = pages.size, state = pagerState) { pageId ->
            val page = pages[pageId]
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier
                        .padding(top = 24.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = stringResource(id = page.titleId), fontSize = 24.sp, modifier = Modifier
                            .align(
                                Alignment.CenterVertically
                            )
                            .padding(start = 16.dp, end = 16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                        .weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = page.iconId),
                        contentDescription = "welcome icon",
                        tint = Color.Unspecified,
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }
                Text(
                    text = stringResource(id = page.massageId),
                    Modifier.padding(start = 32.dp, end = 32.dp).align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier
                    .padding(top = 48.dp, bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                (pages.indices).forEach { page ->
                    Spacer(modifier = Modifier.width(16.dp))
                    if (page == pagerState.targetPage)
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = "",
                            Modifier.size(12.dp),
                            tint = blue
                        )
                    else
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = "",
                            Modifier.size(12.dp),
                            tint = MaterialTheme.colors.onSurface
                        )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (pagerState.targetPage >= pages.size - 1)
                            onSkip()
                        else
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(2f)
                    .padding(top = 16.dp, bottom = 16.dp),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(text = stringResource(id = R.string.next), color = Color.White)
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}