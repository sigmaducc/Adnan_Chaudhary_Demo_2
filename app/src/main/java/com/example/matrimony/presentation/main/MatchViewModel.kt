package com.example.matrimony.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.lifecycle.LiveData
import com.example.matrimony.domain.model.MatchDecision
import com.example.matrimony.domain.model.User
import com.example.matrimony.domain.usecase.ObserveMatchesUseCase
import com.example.matrimony.domain.usecase.UpdateDecisionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MatchViewModel @Inject constructor(
    observeMatches: ObserveMatchesUseCase,
    private val updateDecisionUseCase: UpdateDecisionUseCase
) : ViewModel() {

    val pagedUsers: LiveData<PagingData<User>> = observeMatches()

    fun updateDecision(userId: String, decision: MatchDecision) {
        viewModelScope.launch {
            updateDecisionUseCase(userId, decision)
        }
    }
}