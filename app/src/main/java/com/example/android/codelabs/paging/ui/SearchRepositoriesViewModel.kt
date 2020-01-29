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

package com.example.android.codelabs.paging.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.android.codelabs.paging.data.GithubRepository
import com.example.android.codelabs.paging.model.Repo
import com.example.android.codelabs.paging.model.RepoSearchResult

/**
 * SearchRepositoriesActivity页面的ViewModel
 * 这个ViewModel使用GithubRepository获取数据
 */
class SearchRepositoriesViewModel(private val repository: GithubRepository) : ViewModel() {

    //3.由于输入框变更qery，触发repository.search()返回repoResult
    private val queryLiveData = MutableLiveData<String>()
    //Transformations.map()：对存储在LiveData对象中的值应用一个函数，并传递将结果传递到下游
    private val repoResult: LiveData<RepoSearchResult> = Transformations.map(queryLiveData) {
        repository.search(it)
    }

    //switchMap()和map()一样，只是转发给最新的观察者；返回数据本身，不包装LiveData<>
    //如queryLivaData多次修改，触发epository.search(it)，只会分发最后一次的变更
    //
    // 我怎么感觉用在次场景意义不大，it->it.data又不是耗时操作，理论上不会出现先后错序的问题
    // 此处考虑使用map和switchmap应该考虑的是返回类型是否是LiveData
    //
    //参考：
    // https://www.jianshu.com/p/5a82f14e1b8d
    // https://blog.csdn.net/jdsjlzx/article/details/51730162
    //2.repos和networkErrors观察repository.search()返回的数据
    val repos: LiveData<PagedList<Repo>> = Transformations.switchMap(repoResult) { it -> it.data }
    val networkErrors: LiveData<String> = Transformations.switchMap(repoResult) { it ->
        it.networkErrors
    }

    /**
     * 使用查询字符串搜索repository
     */
    fun searchRepo(queryString: String) {
        //更新查询query字符串，从而发起查询请求
        queryLiveData.postValue(queryString)
    }

    /**
     * 获取最新查询的值
     */
    fun lastQueryValue(): String? = queryLiveData.value
}
