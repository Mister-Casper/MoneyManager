package com.sgcdeveloper.moneymanager.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sgcdeveloper.moneymanager.data.db.dao.*
import com.sgcdeveloper.moneymanager.data.db.entry.*
import com.sgcdeveloper.moneymanager.data.db.util.*

@Database(
    entities = [WalletEntry::class, TransactionEntry::class, RateEntry::class, BudgetEntry::class, RecurringTransactionEntry::class,GoalEntry::class],
    version = 8
)
@TypeConverters(
    CurrencyConverter::class,
    DateConverter::class,
    TransactionCategoryConverter::class,
    ListConverter::class,
    RecurringIntervalConverter::class,
    TransactionEntryConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun transactionDao(): TransactionDao
    abstract fun rateDao(): RateDao
    abstract fun budgetDao(): BudgetDao
    abstract fun recurringTransactionDao(): RecurringTransactionDao
    abstract val goal:GoalDao
}