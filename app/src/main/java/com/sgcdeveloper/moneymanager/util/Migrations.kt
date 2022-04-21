package com.sgcdeveloper.moneymanager.util

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE WalletEntry ADD COLUMN orderPosition INTEGER DEFAULT 0 NOT NULL")
        database.execSQL("UPDATE WalletEntry SET orderPosition = id WHERE orderPosition == 0")
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `RateEntry` (`id` INTEGER PRIMARY KEY NOT NULL, `currency` TEXT NOT NULL, `rate` REAL NOT NULL)")
    }
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `BudgetEntry` (`id` INTEGER PRIMARY KEY NOT NULL, `budgetName` TEXT NOT NULL, `amount` REAL NOT NULL, `categories` TEXT NOT NULL,`color` INTEGER NOT NULL,`date` INTEGER NOT NULL,`period` TEXT NOT NULL)")
    }
}

val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `RecurringTransactionEntry` (`id` INTEGER PRIMARY KEY NOT NULL, `transactionEntry` TEXT NOT NULL, `recurringInterval` TEXT NOT NULL,`fromWalletId` INTEGER NOT NULL,`toWalletId` INTEGER NOT NULL)")
    }
}

val MIGRATION_5_6: Migration = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
      //  database.execSQL("CREATE TABLE IF NOT EXISTS `TransactionCategoryEntry` (`id` INTEGER PRIMARY KEY NOT NULL, `color` TEXT NOT NULL, `icon` TEXT NOT NULL,`description` TEXT NOT NULL,`isDefault` INTEGER NOT NULL, 'isExpense' INTEGER NOT NULL)")
    }
}

val MIGRATION_6_7: Migration = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE TransactionEntry ADD COLUMN fromTransferValue REAL DEFAULT 0 NOT NULL")
        database.execSQL("ALTER TABLE TransactionEntry ADD COLUMN toTransferValue REAL DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_1_2_NEW:Migration = object : Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE TransactionCategoryEntry ADD COLUMN parentId INTEGER default 0 NOT NULL")
    }
}