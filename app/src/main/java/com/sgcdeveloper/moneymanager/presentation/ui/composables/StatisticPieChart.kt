package com.sgcdeveloper.moneymanager.presentation.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.presentation.theme.white
import com.sgcdeveloper.moneymanager.presentation.ui.statistic.formatter

@Composable
fun StatisticPieChart(
    header: String,
    entries: List<PieEntry>,
    colors: List<Int>,
    showMore: () -> Unit,
    isNeedShowMore: Boolean = true,
    onWeeklyStatisticClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {
        Column(modifier = Modifier.padding(top = 12.dp, start = 18.dp, end = 18.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = header, fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Icon(
                    painter = rememberImagePainter(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.weekly_statistic_screen
                        )
                    ),
                    contentDescription = "",
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterEnd)
                        .clickable { onWeeklyStatisticClick() }
                )
            }
            val noDataText = stringResource(id = R.string.no_data_text)
            val textColor = MaterialTheme.colors.onBackground.toArgb()
            AndroidView(factory = { ctx ->
                PieChart(ctx).apply {
                    val dataSet = PieDataSet(entries, "")
                    dataSet.colors = colors
                    dataSet.valueTextColor = white.toArgb()
                    dataSet.valueTextSize = 14f
                    dataSet.valueFormatter = formatter
                    val data = PieData(dataSet)
                    this.setDrawEntryLabels(false)
                    this.data = if (entries.isNotEmpty()) data else null
                    this.invalidate()
                    this.holeRadius = 75.0F
                    this.description.isEnabled = false
                    this.setHoleColor(Color.Transparent.toArgb())
                    this.legend.isWordWrapEnabled = true
                    this.legend.textColor = textColor
                    this.legend.textSize = 12f
                    this.setNoDataText(noDataText)
                    this.setNoDataTextColor(textColor)
                }
            }, modifier = Modifier
                .height(350.dp)
                .fillMaxWidth(), update = {
                val dataSet = PieDataSet(entries, "")
                dataSet.colors = colors
                dataSet.valueTextColor = white.toArgb()
                dataSet.valueTextSize = 14f
                dataSet.valueFormatter = formatter
                val data = PieData(dataSet)
                it.data = if (entries.isNotEmpty()) data else null
                it.invalidate()
            })
            Divider(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxSize()
            )
            if (isNeedShowMore) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showMore()
                    }) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp, bottom = 8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.show_more),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            fontSize = 20.sp
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight,
                            contentDescription = "",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(32.dp)
                        )
                    }
                }
            }
        }
    }
}