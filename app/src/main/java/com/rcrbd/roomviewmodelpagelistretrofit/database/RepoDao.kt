package com.rcrbd.roomviewmodelpagelistretrofit.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rcrbd.roomviewmodelpagelistretrofit.model.Repo

@Dao
interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepos(repos: List<Repo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepo(repo: Repo)

    @Query("SELECT * FROM repos WHERE (name LIKE :queryString) OR (description LIKE :queryString) ORDER BY stars DESC, name ASC")
    fun reposByName(queryString: String) : LiveData<List<Repo>>
}