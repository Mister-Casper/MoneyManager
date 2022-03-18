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