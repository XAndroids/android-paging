/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.codelabs.paging.data

import androidx.paging.LivePagedListBuilder
import android.util.Log
import com.example.android.codelabs.paging.api.GithubService
import com.example.android.codelabs.paging.db.GithubLocalCache
import com.example.android.codelabs.paging.model.RepoSearchResult

/**
 * 处理本地和远程数据源的Repository类
 */
class GithubRepository(
    private val service: GithubService,
    private val cache: GithubLocalCache
) {

    /**
     * 搜索名字匹配查询的仓库
     */
    fun search(query: String): RepoSearchResult {
        Log.d("GithubRepository", "New query: $query")
        //4.分别定义dataSourceFactory用于从缓存中获取数据，boundaryCallback定获取数据到边界是发起网络请求

        // 从本地缓存中获取数据源factory
        val dataSourceFactory = cache.reposByName(query)

        // 每个新的查询创建一个新的BoundaryCallback
        // 这个BoundaryCallback将会观察用户什么时候达到列表的边界，并且使用额外的数据更新数据库
        val boundaryCallback = RepoBoundaryCallback(query, service, cache)
        val networkErrors = boundaryCallback.networkErrors

        //7.通过LivePagedListBuilder传入本地缓存数据源和远程setBoundaryCallback，将ListData<List<Repo>转换成
        //LivaData<PageedList<Repo>>
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()

        // 通过边界回调暴露网络错误
        return RepoSearchResult(data, networkErrors)
    }

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }
}
