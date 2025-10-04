package com.example.matrimony.data.remote.api

import com.example.matrimony.data.remote.dto.RandomUserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomUserService {
    @GET("api/")
    suspend fun getUsers(
        @Query("results") results: Int,
        @Query("page") page: Int,
        @Query("seed") seed: String
    ): Response<RandomUserResponse>
}