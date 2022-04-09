package com.sgcdeveloper.moneymanager.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.sgcdeveloper.moneymanager.data.db.AppDatabase
import com.sgcdeveloper.moneymanager.data.db.TransactionCategoriesDatabase
import com.sgcdeveloper.moneymanager.data.db.entry.BudgetEntry
import com.sgcdeveloper.moneymanager.data.db.util.ListConverter
import com.sgcdeveloper.moneymanager.data.db.util.TransactionCategoryConverter
import com.sgcdeveloper.moneymanager.data.db.util.TransactionEntryConverter
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.repository.AuthRepositoryImpl
import com.sgcdeveloper.moneymanager.data.repository.CurrencyRepositoryImpl
import com.sgcdeveloper.moneymanager.data.repository.MoneyManagerRepositoryImpl
import com.sgcdeveloper.moneymanager.domain.repository.AuthRepository
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.domain.use_case.GetTransactionCategoriesUseCase
import com.sgcdeveloper.moneymanager.util.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context) = context

    @Provides
    fun provideAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository = authRepository

    @Provides
    fun provideCurrencyRepository(appPreferencesHelper: AppPreferencesHelper): CurrencyRepository =
        CurrencyRepositoryImpl(appPreferencesHelper)

    @Provides
    fun provideMoneyManagerRepository(appDatabase: AppDatabase): MoneyManagerRepository =
        MoneyManagerRepositoryImpl(appDatabase)

    @Provides
    @Singleton
    fun providesRoomDatabase(
        app: Application,
        context: Context,
        transactionCategoriesDatabase:TransactionCategoriesDatabase
    ): AppDatabase {
        val get = GetTransactionCategoriesUseCase(context, transactionCategoriesDatabase)
        BudgetEntry.listConverter = ListConverter(get)
        return Room
            .databaseBuilder(app, AppDatabase::class.java, "ApplicationBD")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
            .createFromAsset(DATABASE_DIR)
            .addTypeConverter(ListConverter(get))
            .addTypeConverter(TransactionEntryConverter(get))
            .addTypeConverter(TransactionCategoryConverter(get))
            .build()
    }

    @Provides
    @Singleton
    fun providesTransactionCategoriesDatabase(
        app: Application,
    ): TransactionCategoriesDatabase {
        return Room
            .databaseBuilder(app, TransactionCategoriesDatabase::class.java, "TransactionCategoriesDatabase")
            .createFromAsset(DATABASE_DIR)
            .build()
    }

    companion object {
        private const val DATABASE_DIR = "db/TransactionCategories.db"
    }
}