package com.rcrbd.roomviewmodelpagelistretrofit.database

import android.util.Log
import androidx.lifecycle.LiveData
import com.rcrbd.roomviewmodelpagelistretrofit.model.Repo
import java.util.concurrent.Executor

class GithubLocalCache(
    private val repoDao: RepoDao,
    private val ioExecutor: Executor
) {

    fun insertRepos(repos: List<Repo>, insertFinished: () -> Unit) {
        Log.d("GithubLocalCache", "inserting ${repos.size} repos")
        ioExecutor.execute {
            repoDao.insertRepos(repos)
            insertFinished()
        }
    }

    fun reposByName(name: String) : LiveData<List<Repo>> {
        return repoDao.reposByName()
    }
}