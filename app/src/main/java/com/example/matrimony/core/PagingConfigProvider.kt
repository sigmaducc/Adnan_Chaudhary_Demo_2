package com.example.matrimony.core

import androidx.paging.PagingConfig

object PagingConfigProvider {
    fun create(): PagingConfig = PagingConfig(
        pageSize = Constants.PAGE_SIZE,
        initialLoadSize = Constants.PAGING_INITIAL_LOAD_SIZE,
        prefetchDistance = Constants.PAGING_PREFETCH_DISTANCE,
        enablePlaceholders = false,
        maxSize = Constants.PAGING_MAX_SIZE
    )
}