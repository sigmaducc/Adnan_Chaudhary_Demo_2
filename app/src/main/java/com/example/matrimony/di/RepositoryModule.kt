package com.example.matrimony.di

import com.example.matrimony.data.local.dao.UserDao
import com.example.matrimony.data.local.database.AppDatabase
import com.example.matrimony.data.remote.api.RandomUserService
import com.example.matrimony.data.repository.DefaultMatchRepository
import com.example.matrimony.domain.repository.MatchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideMatchRepository(
        db: AppDatabase,
        userDao: UserDao,
        service: RandomUserService,
        @IoDispatcher io: CoroutineDispatcher
    ): MatchRepository = DefaultMatchRepository(db, userDao, service, io)
}