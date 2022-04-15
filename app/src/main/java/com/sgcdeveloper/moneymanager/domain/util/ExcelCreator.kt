package com.sgcdeveloper.moneymanager.domain.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.sgcdeveloper.moneymanager.R
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.apache.poi.hssf.usermodel.HSSFRichTextString
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.inject.Inject


class ExcelCreator @Inject constructor(
    private val context: Context,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val moneyManagerRepository: MoneyManagerRepository
) {

    suspend operator fun invoke(): Uri = CoroutineScope(Dispatchers.IO).async {
        val transactions = getTransactionsUseCase()
        val wallets = moneyManagerRepository.getWalletsOnce().associateBy { it.id }

        val workbook = HSSFWorkbook()
        val firstSheet = workbook.createSheet("Transactions")
        val rowA = firstSheet.createRow(0)
        val cell1 = rowA.createCell(0)
        cell1.setCellValue(HSSFRichTextString(context.getString(R.string.export_data_title_id)))
        val cell2 = rowA.createCell(1)
        cell2.setCellValue(HSSFRichTextString(context.getString(R.string.export_data_title_date)))
        val cell3 = rowA.createCell(2)
        cell3.setCellValue(HSSFRichTextString(context.getString(R.string.export_data_title_type)))
        val cell4 = rowA.createCell(3)
        cell4.setCellValue(HSSFRichTextString(context.getString(R.string.export_data_title_category)))
        val cell5 = rowA.createCell(4)
        cell5.setCellValue(HSSFRichTextString(context.getString(R.string.export_data_title_money)))
        val cell6 = rowA.createCell(5)
        cell6.setCellValue(HSSFRichTextString(context.getString(R.string.export_data_title_description)))
        val cell7 = rowA.createCell(6)
        cell7.setCellValue(HSSFRichTextString(context.getString(R.string.export_data_title_currency)))

        transactions.forEachIndexed { index, transaction ->
            val nextRow = firstSheet.createRow(index+1)
            val nextCell1 = nextRow.createCell(0)
            nextCell1.setCellValue(HSSFRichTextString((transactions.size - index).toString()))
            val nextCell2 = nextRow.createCell(1)
            nextCell2.setCellValue(HSSFRichTextString(transaction.date.toDayMonthString()))
            val nextCell3 = nextRow.createCell(2)
            nextCell3.setCellValue(HSSFRichTextString(context.getString(transaction.transactionType.stringRes)))
            val nextCell4 = nextRow.createCell(3)
            nextCell4.setCellValue(HSSFRichTextString(transaction.category.description))
            val nextCell5 = nextRow.createCell(4)
            nextCell5.setCellValue(HSSFRichTextString(transaction.value.toString()))
            val nextCell6 = nextRow.createCell(5)
            nextCell6.setCellValue(HSSFRichTextString(transaction.description))
            val nextCell7 = nextRow.createCell(6)
            nextCell7.setCellValue(HSSFRichTextString(wallets[transaction.fromWalletId]!!.currency.code))
        }

        val qponFile = File(context.filesDir, "money_manager_transactions.xls")
        val fileWriter = FileWriter(qponFile)

        try {
            workbook.write(qponFile)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fileWriter.flush()
            fileWriter.close()
        }

        val csvUri =
            FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", qponFile)
        return@async csvUri!!
    }.await()
}