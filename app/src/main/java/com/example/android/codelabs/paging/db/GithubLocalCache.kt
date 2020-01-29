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

package com.example.android.codelabs.paging.db

import android.util.Log
import androidx.paging.DataSource
import com.example.android.codelabs.paging.model.Repo
import java.util.concurrent.Executor

/**
 * 处理本地数据源Dao的类。确保方法在正确的执行器中被触发
 */
class GithubLocalCache(
        private val repoDao: RepoDao,
        private val ioExecutor: Executor
) {

    /**
     * 向数据库中插入仓库列表，在后台线程
     */
    fun insert(repos: List<Repo>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            Log.d("GithubLocalCache", "inserting ${repos.size} repos")
            repoDao.insert(repos)
            insertFinished()
        }
    }

    /**
     * 使用仓库的名称，从Dao请求LiveData<List<Repo>>。如果名字包含多个词使用空格分隔，然后我们模拟GitHub API的
     * 行为，允许单词之间有任何字符
     * @param name 仓库名称
     */
    fun reposByName(name: String): DataSource.Factory<Int, Repo> {
        // appending '%' so we can allow other characters to be before and after the query string
        val query = "%${name.replace(' ', '%')}%"
        return repoDao.reposByName(query)
    }
}
