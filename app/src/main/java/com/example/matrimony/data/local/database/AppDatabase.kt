package com.example.matrimony.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.matrimony.core.Constants.DATABASE_VERSION
import com.example.matrimony.data.local.dao.UserDao
import com.example.matrimony.data.local.dao.RemoteKeysDao
import com.example.matrimony.data.local.entity.UserEntity
import com.example.matrimony.data.local.entity.RemoteKeysEntity

@Database(
    entities = [UserEntity::class, RemoteKeysEntity::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}