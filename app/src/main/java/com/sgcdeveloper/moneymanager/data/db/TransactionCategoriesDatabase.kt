package com.sgcdeveloper.moneymanager.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sgcdeveloper.moneymanager.data.db.dao.TransactionCategoryDao
import com.sgcdeveloper.moneymanager.data.db.entry.TransactionCategoryEntry

@Database(
    entities = [TransactionCategoryEntry::class],
    version = 2
)
abstract class TransactionCategoriesDatabase : RoomDatabase() {
    abstract fun transactionCategoryDao(): TransactionCategoryDao
}