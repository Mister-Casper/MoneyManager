package com.sgcdeveloper.moneymanager.di

import android.content.Context
import com.sgcdeveloper.moneymanager.data.repository.AuthRepositoryImpl
import com.sgcdeveloper.moneymanager.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context) = context

    @Provides
    fun provideAuthRepository(authRepository: AuthRepositoryImpl):AuthRepository = authRepository

}