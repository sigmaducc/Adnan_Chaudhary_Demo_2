package com.example.matrimony.domain.usecase

import androidx.paging.PagingData
import com.example.matrimony.domain.model.User
import com.example.matrimony.domain.repository.MatchRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveMatchesUseCase @Inject constructor(
    private val repository: MatchRepository
) {
    operator fun invoke(): Flow<PagingData<User>>
        = repository.pagedUsers()
}