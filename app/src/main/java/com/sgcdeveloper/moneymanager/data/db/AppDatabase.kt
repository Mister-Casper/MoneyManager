package com.sgcdeveloper.moneymanager.data.db

 import androidx.room.Database
 import androidx.room.RoomDatabase
 import androidx.room.TypeConverters
 import com.sgcdeveloper.moneymanager.data.db.dao.RateDao
 import com.sgcdeveloper.moneymanager.data.db.dao.TransactionDao
 import com.sgcdeveloper.moneymanager.data.db.dao.WalletDao
 import com.sgcdeveloper.moneymanager.data.db.entry.RateEntry
 import com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry
 import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
 import com.sgcdeveloper.moneymanager.data.db.util.CurrencyConverter
 import com.sgcdeveloper.moneymanager.data.db.util.DateConverter
 import com.sgcdeveloper.moneymanager.data.db.util.TransactionCategoryConverter

@Database(
    entities = [WalletEntry::class, TransactionEntry::class,RateEntry::class],
    version = 3
)
@TypeConverters(CurrencyConverter::class, DateConverter::class, TransactionCategoryConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun transactionDao(): TransactionDao
    abstract fun rateDao(): RateDao
}