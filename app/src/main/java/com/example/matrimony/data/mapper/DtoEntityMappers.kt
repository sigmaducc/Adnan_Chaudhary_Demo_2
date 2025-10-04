package com.example.matrimony.data.mapper

import com.example.matrimony.data.local.entity.UserEntity
import com.example.matrimony.data.remote.dto.ResultDto

fun ResultDto.toEntity(nowEpochMs: Long, apiOrderIndex: Long = 0L): UserEntity? {
    val id = this.login?.uuid ?: return null
    val fullName = listOfNotNull(this.name?.first, this.name?.last).joinToString(" ")
    val city = this.location?.city ?: ""
    val state = this.location?.state ?: ""
    val country = this.location?.country ?: ""
    val age = this.dob?.age ?: 0
    val large = this.picture?.large ?: ""
    val thumb = this.picture?.thumbnail ?: ""

    return UserEntity(
        id = id,
        apiOrderIndex = apiOrderIndex,
        fullName = fullName,
        age = age,
        city = city,
        state = state,
        country = country,
        imageLargeUrl = large,
        imageThumbUrl = thumb,
        decision = "PENDING",
        lastUpdatedEpochMs = nowEpochMs
    )
}