package com.example.matrimony.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.paging.map
import com.example.matrimony.data.local.dao.UserDao
import com.example.matrimony.data.remote.api.RandomUserService
import com.example.matrimony.domain.mapper.toDomain
import com.example.matrimony.domain.model.MatchDecision
import com.example.matrimony.domain.model.User
import com.example.matrimony.domain.repository.MatchRepository
import com.example.matrimony.core.Constants
import com.example.matrimony.data.local.database.AppDatabase
import com.example.matrimony.data.paging.UsersRemoteMediator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DefaultMatchRepository(
    private val db: AppDatabase,
    private val userDao: UserDao,
    private val service: RandomUserService,
    private val ioDispatcher: CoroutineDispatcher
) : MatchRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun pagedUsers(): Flow<PagingData<User>> =
        Pager(
            config = PagingConfig(
                pageSize = Constants.PAGE_SIZE,
                initialLoadSize = Constants.PAGE_SIZE,
                prefetchDistance = 3,
                enablePlaceholders = false
            ),
            remoteMediator = UsersRemoteMediator(
                db = db,
                service = service,
                seed = Constants.DEFAULT_SEED
            ),
            pagingSourceFactory = { userDao.pagingSource() }
        ).flow.map { paging -> paging.map { it.toDomain() } }

    override suspend fun updateDecision(userId: String, decision: MatchDecision) {
        withContext(context = ioDispatcher) {
            userDao.updateDecision(
                userId = userId,
                decision = decision.name
            )
        }
    }
}