package com.example.matrimony.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.matrimony.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY apiOrderIndex ASC, id ASC")
    fun observeAll(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(users: List<UserEntity>)

    @Query("UPDATE users SET decision = :decision WHERE id = :userId")
    suspend fun updateDecision(userId: String, decision: String)

    @Query("DELETE FROM users")
    suspend fun clearAll()

    @Query("SELECT * FROM users WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<UserEntity>

    @Query("SELECT * FROM users ORDER BY apiOrderIndex ASC, id ASC")
    fun pagingSource(): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM users ORDER BY apiOrderIndex DESC, id DESC LIMIT 1")
    suspend fun getLastByOrder(): UserEntity?
}