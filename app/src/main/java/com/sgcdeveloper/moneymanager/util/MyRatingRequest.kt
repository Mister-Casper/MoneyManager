package com.sgcdeveloper.moneymanager.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.sgcdeveloper.moneymanager.R
import java.text.SimpleDateFormat
import java.util.*

class MyRatingRequest {
    companion object {
        fun with(context: Context): Builder {
            return Builder(context)
        }
    }

    class Builder(var context: Context) {
        private val btn_agree: Button
        private val btn_done: Button
        private val btn_later: Button
        var settings: SharedPreferences
        var listener: ClickListener? = null
        private var isCancelable = true
        var v: View
        private fun getNextDate(days: Int): String {
            val format1 = SimpleDateFormat("yyyy-MM-dd")
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, days)
            return format1.format(calendar.time)
        }

        private val todayDate: String
            private get() {
                val format1 = SimpleDateFormat("yyyy-MM-dd")
                val calendar = Calendar.getInstance()
                return format1.format(calendar.time)
            }

        fun message(message: String?): Builder {
            val title = v.findViewById<View>(R.id.tv_title) as TextView
            title.text = message
            return this
        }

        fun scheduleAfter(days: Int): Builder {
            scheduleAfter = days
            return this
        }

        fun agreeButtonText(yesButtonText: String?): Builder {
            btn_agree.text = yesButtonText
            return this
        }

        fun doneButtonText(doneButtonText: String?): Builder {
            btn_done.text = doneButtonText
            return this
        }

        fun laterButtonText(laterButtonText: String?): Builder {
            btn_later.text = laterButtonText
            return this
        }

        fun backgroundColor(color: Int): Builder {
            val layout = v.findViewById<View>(R.id.lay_full) as LinearLayout
            layout.setBackgroundColor(color)
            return this
        }

        fun backgroundResource(res: Int): Builder {
            val layout = v.findViewById<View>(R.id.lay_full) as LinearLayout
            layout.setBackgroundResource(res)
            return this
        }

        fun agreeButtonSeletor(seletor: Int): Builder {
            btn_agree.setBackgroundResource(seletor)
            return this
        }

        fun doneButtonSeletor(seletor: Int): Builder {
            btn_done.setBackgroundResource(seletor)
            return this
        }

        fun laterButtonSeletor( seletor: Int): Builder {
            btn_later.setBackgroundResource(seletor)
            return this
        }

        fun agreeButtonTextColor(color: Int): Builder {
            btn_agree.setTextColor(color)
            return this
        }

        fun doneButtonTextColor(color: Int): Builder {
            btn_done.setTextColor(color)
            return this
        }

        fun laterButtonTextColor(color: Int): Builder {
            btn_later.setTextColor(color)
            return this
        }

        fun delay(timeInMillis: Long): Builder {
            delayTime = timeInMillis
            return this
        }

        fun cancelable(isCancelable: Boolean): Builder {
            this.isCancelable = isCancelable
            return this
        }

        fun listener(listener: ClickListener): Builder {
            this.listener = listener
            return this
        }

        fun register(): Builder {
            if (settings.getBoolean("isLaterEnable", false) && todayDate.equals(
                    settings.getString("later_date", ""),
                    ignoreCase = true
                )
            ) {
                initRunnable()
                dismissHandler.postDelayed(dialogRunnable!!, delayTime)
            } else if (settings.getBoolean("isFirstTime", true)) {
                initRunnable()
                dismissHandler.postDelayed(dialogRunnable!!, delayTime)
                val editor = settings.edit()
                editor.putBoolean("isFirstTime", false)
                editor.putBoolean("isLaterEnable", true)
                editor.apply()
            }
            return this
        }

        private fun initRunnable() {
            dialogRunnable = Runnable {
                if ((context as Activity).isFinishing) {
                    return@Runnable
                }
                val ratingDialog: AlertDialog = AlertDialog.Builder(context)
                    .setView(v)
                    .setCancelable(isCancelable)
                    .create()
                ratingDialog.show()
                btn_agree.setOnClickListener {
                    val uri =
                        Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                    ratingDialog.dismiss()
                    listener!!.onAgreeButtonClick()
                }
                btn_done.setOnClickListener {
                    val editor = settings.edit()
                    editor.putBoolean("isLaterEnable", false)
                    editor.commit()
                    ratingDialog.dismiss()
                    listener!!.onDoneButtonClick()
                }
                btn_later.setOnClickListener {
                    val editor = settings.edit()
                    editor.putBoolean("isLaterEnable", true)
                    editor.putString("later_date", getNextDate(scheduleAfter))
                    editor.commit()
                    ratingDialog.dismiss()
                    listener!!.onLaterButtonClick()
                }
            }
        }

        companion object {
            private var delayTime: Long = 1000 * 120
            private var scheduleAfter = 5
            var dismissHandler = Handler()
            var dialogRunnable: Runnable? = null
        }

        init {
            v = LayoutInflater.from(context).inflate(R.layout.dialog_view, null)
            btn_agree = v.findViewById<View>(R.id.btn_yes) as Button
            btn_done = v.findViewById<View>(R.id.btn_done) as Button
            btn_later = v.findViewById<View>(R.id.btn_later) as Button
            settings = context.getSharedPreferences("ReviewDialogPref", Context.MODE_PRIVATE)
        }
    }

    interface ClickListener {
        fun onAgreeButtonClick()
        fun onDoneButtonClick()
        fun onLaterButtonClick()
    }
}