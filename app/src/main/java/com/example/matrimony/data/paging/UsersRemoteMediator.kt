package com.example.matrimony.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.matrimony.core.Constants
import com.example.matrimony.data.local.database.AppDatabase
import com.example.matrimony.data.local.entity.RemoteKeysEntity
import com.example.matrimony.data.local.entity.UserEntity
import com.example.matrimony.data.mapper.toEntity
import com.example.matrimony.data.remote.api.RandomUserService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class UsersRemoteMediator(
    private val db: AppDatabase,
    private val service: RandomUserService,
    private val seed: String = Constants.DEFAULT_SEED
) : RemoteMediator<Int, UserEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) return MediatorResult.Success(endOfPaginationReached = true)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) return MediatorResult.Success(endOfPaginationReached = true)
                nextKey
            }
        }

        return try {
            val response = service.getUsers(
                results = state.config.pageSize.takeIf { it > 0 } ?: Constants.PAGE_SIZE,
                page = page,
                seed = seed
            )
            if (!response.isSuccessful) throw HttpException(response)
            val body = response.body()
            val now = System.currentTimeMillis()
            val incoming: List<UserEntity> = (body?.results ?: emptyList())
                .mapIndexedNotNull { indexInPage, dto ->
                    val globalIndex = ((page - 1L) * (state.config.pageSize.toLong())) + indexInPage.toLong()
                    dto.toEntity(now, apiOrderIndex = globalIndex)
                }
            val endOfPaginationReached = incoming.isEmpty()

            db.withTransaction {
                val userDao = db.userDao()
                val keysDao = db.remoteKeysDao()
                if (loadType == LoadType.REFRESH) {
                    keysDao.clearRemoteKeys()
                }

                val ids = incoming.map { it.id }
                val existing = userDao.getByIds(ids).associateBy { it.id }
                val merged = incoming.map { inc ->
                    val prev = existing[inc.id]
                    if (prev != null) {
                        inc.copy(
                            decision = prev.decision,
                            apiOrderIndex = prev.apiOrderIndex
                        )
                    } else inc
                }

                userDao.upsertAll(merged)

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val remoteKeys = merged.map { RemoteKeysEntity(userId = it.id, prevKey = prevKey, nextKey = nextKey) }
                keysDao.insertAll(remoteKeys)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (io: IOException) {
            MediatorResult.Error(io)
        } catch (he: HttpException) {
            MediatorResult.Error(he)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, UserEntity>): RemoteKeysEntity? {
        val lastItemFromState = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
        if (lastItemFromState != null) {
            return db.remoteKeysDao().remoteKeysUserId(lastItemFromState.id)
        }

        val lastFromDb = db.userDao().getLastByOrder()
        return lastFromDb?.let { db.remoteKeysDao().remoteKeysUserId(it.id) }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, UserEntity>): RemoteKeysEntity? {
        val firstItem = state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
        return firstItem?.let { db.remoteKeysDao().remoteKeysUserId(it.id) }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, UserEntity>): RemoteKeysEntity? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorItem = state.closestItemToPosition(anchorPosition) ?: return null
        return db.remoteKeysDao().remoteKeysUserId(anchorItem.id)
    }
}


