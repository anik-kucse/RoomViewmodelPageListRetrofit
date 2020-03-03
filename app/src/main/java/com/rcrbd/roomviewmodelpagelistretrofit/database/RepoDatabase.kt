package com.rcrbd.roomviewmodelpagelistretrofit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rcrbd.roomviewmodelpagelistretrofit.model.Repo

@Database(
    entities = [Repo::class],
    version = 1,
    exportSchema = false
)
abstract class RepoDatabase : RoomDatabase() {
    abstract fun repoDao() : RepoDao

    companion object{
        @Volatile
        private var INSTANCE: RepoDatabase? = null

        fun getInstance(context: Context) : RepoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it}
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                RepoDatabase::class.java, "git_database").build()
    }
}