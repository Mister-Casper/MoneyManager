package com.sgcdeveloper.moneymanager.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sgcdeveloper.moneymanager.data.db.dao.BudgetDao
import com.sgcdeveloper.moneymanager.data.db.dao.RateDao
import com.sgcdeveloper.moneymanager.data.db.dao.TransactionDao
import com.sgcdeveloper.moneymanager.data.db.dao.WalletDao
import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry
import com.sgcdeveloper.moneymanager.data.db.entry.RateEntry
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
import com.sgcdeveloper.moneymanager.data.db.util.CurrencyConverter
import com.sgcdeveloper.moneymanager.data.db.util.DateConverter
import com.sgcdeveloper.moneymanager.data.db.util.ListConverter
import com.sgcdeveloper.moneymanager.data.db.util.TransactionCategoryConverter

@Database(
    entities = [WalletEntry::class, TransactionEntry::class, RateEntry::class, BudgetEntry::class],
    version = 4
)
@TypeConverters(
    CurrencyConverter::class,
    DateConverter::class,
    TransactionCategoryConverter::class,
    ListConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun transactionDao(): TransactionDao
    abstract fun rateDao(): RateDao
    abstract fun budgetDao(): BudgetDao
}