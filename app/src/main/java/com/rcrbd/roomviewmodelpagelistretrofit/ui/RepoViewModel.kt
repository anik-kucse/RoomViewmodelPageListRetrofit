package com.rcrbd.roomviewmodelpagelistretrofit.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.rcrbd.roomviewmodelpagelistretrofit.data.GithubRepository
import com.rcrbd.roomviewmodelpagelistretrofit.model.Repo
import com.rcrbd.roomviewmodelpagelistretrofit.model.RepoSearchResult

class RepoViewModel(
    private val repository: GithubRepository
) : ViewModel() {

    private val queryLiveData = MutableLiveData<String>()
    private val repoResult: LiveData<RepoSearchResult> = Transformations.map(queryLiveData) {
        repository.search(it)
    }

    val repos : LiveData<PagedList<Repo>> = Transformations.switchMap(repoResult) {
        it.data
    }

//    val reposOnly : LiveData<PagedList<Repo>> = repository.serachWithoutNetwork(query = "Android")

    val networkErrors: LiveData<String> = Transformations.switchMap(repoResult) {
        it.networkErrors
    }

    fun searchRepo(query: String) {
        queryLiveData.postValue(query)
    }

    fun lastQueryValue(): String? = queryLiveData.value
}