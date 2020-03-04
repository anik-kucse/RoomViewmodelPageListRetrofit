package com.rcrbd.roomviewmodelpagelistretrofit.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.rcrbd.roomviewmodelpagelistretrofit.api.GithubApiService
import com.rcrbd.roomviewmodelpagelistretrofit.api.searchRepos
import com.rcrbd.roomviewmodelpagelistretrofit.database.GithubLocalCache
import com.rcrbd.roomviewmodelpagelistretrofit.model.Repo
import com.rcrbd.roomviewmodelpagelistretrofit.model.RepoSearchResult

class GithubRepository(
    private val service: GithubApiService,
    private val cache: GithubLocalCache
) {

    fun search(query: String) :RepoSearchResult {
        Log.e("GithubRepository", "New query: $query")

        val dataSourceFactory = cache.reposByName(query)
        val boundaryCallback = RepoBoundaryCallback(query, service, cache)
        val netWorkErrors = boundaryCallback.networkError

        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()
        return RepoSearchResult(data, netWorkErrors)
    }

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }

}