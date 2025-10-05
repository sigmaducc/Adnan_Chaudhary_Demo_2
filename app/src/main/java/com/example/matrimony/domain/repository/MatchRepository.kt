package com.example.matrimony.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.matrimony.domain.model.MatchDecision
import com.example.matrimony.domain.model.User

interface MatchRepository {
    fun pagedUsers(): LiveData<PagingData<User>>

    suspend fun updateDecision(userId: String, decision: MatchDecision)
}