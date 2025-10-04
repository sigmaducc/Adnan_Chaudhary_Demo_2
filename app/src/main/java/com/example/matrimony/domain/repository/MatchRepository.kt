package com.example.matrimony.domain.repository

import androidx.paging.PagingData
import com.example.matrimony.domain.model.MatchDecision
import com.example.matrimony.domain.model.User
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    fun pagedUsers(): Flow<PagingData<User>>

    suspend fun updateDecision(userId: String, decision: MatchDecision)
}