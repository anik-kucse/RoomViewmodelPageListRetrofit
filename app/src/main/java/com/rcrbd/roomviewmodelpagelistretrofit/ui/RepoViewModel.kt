package com.rcrbd.roomviewmodelpagelistretrofit.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.rcrbd.roomviewmodelpagelistretrofit.data.GithubRepository
import com.rcrbd.roomviewmodelpagelistretrofit.model.Repo
import com.rcrbd.roomviewmodelpagelistretrofit.model.RepoSearchResult

class RepoViewModel(
    private val repository: GithubRepository
) : ViewModel() {
    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val queryLiveData = MutableLiveData<String>()
    private val repoResult: LiveData<RepoSearchResult> = Transformations.map(queryLiveData) {
        repository.search(it)
    }

    val repos : LiveData<List<Repo>> = Transformations.switchMap(repoResult) {
        it.data
    }

    val reposOnly : LiveData<List<Repo>> = repository.serachWithoutNetwork(query = "Android")

    val networkErrors: LiveData<String> = Transformations.switchMap(repoResult) {
        it.networkErrors
    }

    fun searchRepo(query: String) {
        queryLiveData.postValue(query)
    }

    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int ) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            val immutableQuery = lastQueryValue()
            if (immutableQuery != null) {
                repository.requestMore(immutableQuery)
            }
        }
    }

    fun lastQueryValue(): String? = queryLiveData.value
}