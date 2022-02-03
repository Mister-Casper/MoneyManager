package com.sgcdeveloper.moneymanager.data.db

 import androidx.room.Database
 import androidx.room.RoomDatabase
 import androidx.room.TypeConverters
 import com.sgcdeveloper.moneymanager.data.db.dao.WalletDao
 import com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry
 import com.sgcdeveloper.moneymanager.data.db.util.CurrencyConverter

@Database(
    entities = [WalletEntry::class],
    version = 1
)
@TypeConverters(CurrencyConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
}