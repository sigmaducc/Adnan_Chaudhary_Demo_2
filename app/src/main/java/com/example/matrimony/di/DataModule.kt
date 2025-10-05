package com.example.matrimony.di

import androidx.room.Room
import com.example.matrimony.core.Constants
import com.example.matrimony.data.local.dao.RemoteKeysDao
import com.example.matrimony.data.local.dao.UserDao
import com.example.matrimony.data.local.database.AppDatabase
import com.example.matrimony.data.remote.api.RandomUserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import android.content.Context

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "matrimony.db").build()

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideRemoteKeysDao(db: AppDatabase): RemoteKeysDao = db.remoteKeysDao()

    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        return OkHttpClient.Builder().addInterceptor(logging).build()
    }

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    @Provides @Singleton
    fun provideRandomUserService(retrofit: Retrofit): RandomUserService =
        retrofit.create(RandomUserService::class.java)
}