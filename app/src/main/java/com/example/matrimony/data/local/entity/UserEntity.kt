package com.example.matrimony.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["apiOrderIndex"]),
        Index(value = ["lastUpdatedEpochMs"])
    ]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val apiOrderIndex: Long,
    val fullName: String,
    val age: Int,
    val city: String,
    val state: String,
    val country: String,
    val imageLargeUrl: String,
    val imageThumbUrl: String,
    val decision: String,
    val lastUpdatedEpochMs: Long
)