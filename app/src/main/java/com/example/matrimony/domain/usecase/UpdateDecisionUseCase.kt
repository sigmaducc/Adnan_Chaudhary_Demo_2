package com.example.matrimony.domain.usecase

import com.example.matrimony.domain.model.MatchDecision
import com.example.matrimony.domain.repository.MatchRepository
import javax.inject.Inject

class UpdateDecisionUseCase @Inject constructor(
    private val repository: MatchRepository
) {
    suspend operator fun invoke(userId: String, decision: MatchDecision)
        = repository.updateDecision(userId, decision)
}