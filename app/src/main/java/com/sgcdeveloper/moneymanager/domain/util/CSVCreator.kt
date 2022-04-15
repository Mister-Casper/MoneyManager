package com.sgcdeveloper.moneymanager.domain.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.File
import java.io.FileWriter
import javax.inject.Inject


class CSVCreator @Inject constructor(
    private val context: Context,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val moneyManagerRepository: MoneyManagerRepository
) {

    private val CSV_HEADER = context.getString(R.string.csv_title)

    suspend operator fun invoke(): Uri = CoroutineScope(Dispatchers.IO).async {
        val transactions = getTransactionsUseCase()
        val wallets = moneyManagerRepository.getWalletsOnce().associateBy { it.id }

        val qponFile = File(context.filesDir, "money_manager_transactions.csv")
        val fileWriter = FileWriter(qponFile)

        try {
            fileWriter.write(CSV_HEADER)
            fileWriter.write("\n")

            transactions.forEachIndexed { index, transaction ->
                fileWriter.write((transactions.size - index).toString())
                fileWriter.write(",")
                fileWriter.write(transaction.date.toDayMonthString())
                fileWriter.write(",")
                fileWriter.write(context.getString(transaction.transactionType.stringRes))
                fileWriter.write(",")
                fileWriter.write(transaction.category.description)
                fileWriter.write(",")
                fileWriter.write(transaction.value.toString())
                fileWriter.write(",")
                fileWriter.write(transaction.description)
                fileWriter.write(",")
                fileWriter.write(wallets[transaction.fromWalletId]!!.currency.code)
                fileWriter.write("\n")
            }

            FirebaseAnalytics.getInstance(context).logEvent("write_csv_successfully", null)
        } catch (e: Exception) {
            FirebaseAnalytics.getInstance(context).logEvent("write_csv_error", null)
            e.printStackTrace()
        } finally {
            fileWriter.close()
        }

        val csvUri =
            FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", qponFile)
        return@async csvUri!!
    }.await()
}