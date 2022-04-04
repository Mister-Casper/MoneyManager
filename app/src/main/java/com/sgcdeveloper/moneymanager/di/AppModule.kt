package com.sgcdeveloper.moneymanager.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.sgcdeveloper.moneymanager.data.db.AppDatabase
import com.sgcdeveloper.moneymanager.data.prefa.AppPreferencesHelper
import com.sgcdeveloper.moneymanager.data.repository.AuthRepositoryImpl
import com.sgcdeveloper.moneymanager.data.repository.CurrencyRepositoryImpl
import com.sgcdeveloper.moneymanager.data.repository.MoneyManagerRepositoryImpl
import com.sgcdeveloper.moneymanager.domain.repository.AuthRepository
import com.sgcdeveloper.moneymanager.domain.repository.CurrencyRepository
import com.sgcdeveloper.moneymanager.domain.repository.MoneyManagerRepository
import com.sgcdeveloper.moneymanager.util.MIGRATION_1_2
import com.sgcdeveloper.moneymanager.util.MIGRATION_2_3
import com.sgcdeveloper.moneymanager.util.MIGRATION_3_4
import com.sgcdeveloper.moneymanager.util.MIGRATION_4_5
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
    fun provideCurrencyRepository(appPreferencesHelper: AppPreferencesHelper): CurrencyRepository = CurrencyRepositoryImpl(appPreferencesHelper)

    @Provides
    fun provideMoneyManagerRepository(appDatabase: AppDatabase): MoneyManagerRepository =
        MoneyManagerRepositoryImpl(appDatabase)

    @Provides
    @Singleton
    fun providesRoomDatabase(app: Application): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, "ApplicationBD")
            .addMigrations(MIGRATION_1_2,MIGRATION_2_3,MIGRATION_3_4,MIGRATION_4_5)
            .build()
    }
}