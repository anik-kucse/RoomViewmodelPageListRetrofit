package com.rcrbd.roomviewmodelpagelistretrofit.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.rcrbd.roomviewmodelpagelistretrofit.api.GithubApiService
import com.rcrbd.roomviewmodelpagelistretrofit.api.searchRepos
import com.rcrbd.roomviewmodelpagelistretrofit.database.GithubLocalCache
import com.rcrbd.roomviewmodelpagelistretrofit.model.Repo

class RepoBoundaryCallback(
    private val queryString: String,
    private val service: GithubApiService,
    private val cache: GithubLocalCache
) : PagedList.BoundaryCallback<Repo>() {

    private var lastRequestedPageNumber = 1
    private val _netWorkErrors = MutableLiveData<String>()
    private var isRequestInProgress = false

    val networkError : LiveData<String> get() = _netWorkErrors

    override fun onZeroItemsLoaded() {
//        super.onZeroItemsLoaded()
        requestAndSaveData(queryString)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Repo) {
//        super.onItemAtEndLoaded(itemAtEnd)
        requestAndSaveData(queryString)
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
                _netWorkErrors.postValue(it)
                isRequestInProgress = false
            }
        )
    }


    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }
}