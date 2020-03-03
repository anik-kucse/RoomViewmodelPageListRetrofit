package com.rcrbd.roomviewmodelpagelistretrofit.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.rcrbd.roomviewmodelpagelistretrofit.api.GithubApiService
import com.rcrbd.roomviewmodelpagelistretrofit.api.searchRepos
import com.rcrbd.roomviewmodelpagelistretrofit.database.GithubLocalCache
import com.rcrbd.roomviewmodelpagelistretrofit.model.RepoSearchResult

class GithubRepository(
    private val service: GithubApiService,
    private val cache: GithubLocalCache
) {
    private var lastRequestedPageNumber = 1
    private var netWorkErrors = MutableLiveData<String>()
    private var isRequestInProgress = false

    fun search(query: String) :RepoSearchResult {
        Log.e("GithubRepository", "New query: $query")
        lastRequestedPageNumber = 1
        requestAndSaveData(query)

        val data = cache.reposByName(query)

        return RepoSearchResult(data, netWorkErrors)
    }
    fun requestMore(query: String) {
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String) {
        if (isRequestInProgress) return

        isRequestInProgress = true
        searchRepos(
            service,
            query,
            lastRequestedPageNumber,
            NETWORK_PAGE_SIZE,
            {
                cache.insertRepos(it) {
                    lastRequestedPageNumber ++
                    isRequestInProgress = false
                }
            },
            {
                netWorkErrors.postValue(it)
                isRequestInProgress = false
            }
        )
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }

}