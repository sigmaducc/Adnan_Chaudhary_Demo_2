package com.example.matrimony.di

import android.content.Context
import android.net.ConnectivityManager
import com.example.matrimony.core.images.GlideImageLoader
import com.example.matrimony.core.images.ImageLoader
import com.example.matrimony.core.network.ConnectivityNetworkMonitor
import com.example.matrimony.core.network.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object PlatformModule {
    @Provides @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides @Singleton
    fun provideImageLoader(): ImageLoader = GlideImageLoader()

    @Provides @Singleton
    @IoDispatcher fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Module
@InstallIn(SingletonComponent::class)
abstract class PlatformBindingsModule {
    @Binds @Singleton abstract fun bindNetworkMonitor(impl: ConnectivityNetworkMonitor): NetworkMonitor
}