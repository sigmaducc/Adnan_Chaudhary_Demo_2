package com.example.matrimony.data.remote.dto

data class ResultDto(
    val gender: String?,
    val name: NameDto?,
    val location: LocationDto?,
    val email: String?,
    val login: LoginDto?,
    val dob: DobDto?,
    val phone: String?,
    val cell: String?,
    val id: IdDto?,
    val picture: PictureDto?
)