package com.example.matrimony.domain.mapper

import com.example.matrimony.data.local.entity.UserEntity
import com.example.matrimony.domain.model.MatchDecision
import com.example.matrimony.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    fullName = fullName,
    age = age,
    city = city,
    state = state,
    country = country,
    imageLargeUrl = imageLargeUrl,
    imageThumbUrl = imageThumbUrl,
    decision = MatchDecision.valueOf(decision)
)