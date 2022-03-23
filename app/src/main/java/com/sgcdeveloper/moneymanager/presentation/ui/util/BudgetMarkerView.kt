package com.sgcdeveloper.moneymanager.presentation.ui.util

import android.content.Context
import android.graphics.Canvas
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.model.BudgetGraphEntry

class BudgetMarkerView(context: Context?,layoutResource: Int)  : MarkerView(context,layoutResource) {
    private var money: TextView = findViewById(R.id.money)
    private var date: TextView = findViewById(R.id.date)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e is BudgetGraphEntry) {
            money.text = e.money
            date.text = e.date
        }
        super.refreshContent(e, highlight)
    }

    override fun draw(canvas: Canvas, posx: Float, posy: Float) {
        var posx = posx
        var posy = posy
        posx -= posx / canvas.width * width
        posy -= posy / canvas.height * height
        canvas.translate(posx, posy)
        draw(canvas)
        canvas.translate(-posx, -posy)
    }
}