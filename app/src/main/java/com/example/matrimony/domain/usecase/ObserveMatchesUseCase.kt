package com.example.matrimony.domain.usecase

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.matrimony.domain.model.User
import com.example.matrimony.domain.repository.MatchRepository
import javax.inject.Inject

class ObserveMatchesUseCase @Inject constructor(
    private val repository: MatchRepository
) {
    operator fun invoke(): LiveData<PagingData<User>> = repository.pagedUsers()
}