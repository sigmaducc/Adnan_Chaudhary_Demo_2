package com.example.matrimony.domain.model

data class User(
    val id: String,
    val fullName: String,
    val age: Int,
    val city: String,
    val state: String,
    val country: String,
    val imageLargeUrl: String,
    val imageThumbUrl: String,
    val decision: MatchDecision
)