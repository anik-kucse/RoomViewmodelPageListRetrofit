package com.rcrbd.roomviewmodelpagelistretrofit.ui

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rcrbd.roomviewmodelpagelistretrofit.Injection
import com.rcrbd.roomviewmodelpagelistretrofit.R
import com.rcrbd.roomviewmodelpagelistretrofit.api.GithubApiService
import com.rcrbd.roomviewmodelpagelistretrofit.api.IN_QUALIFIER
import com.rcrbd.roomviewmodelpagelistretrofit.database.RepoDatabase
import com.rcrbd.roomviewmodelpagelistretrofit.model.Repo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: RepoViewModel
    private val adapter = ReposAdapter()
    private lateinit var coroutineScope: CoroutineScope



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(this)).get(RepoViewModel::class.java)

        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        list.addItemDecoration(decoration)

        initAdapter()

        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        supportActionBar?.title = "Only Room + Repo + ViewModel"
        coroutineScope = CoroutineScope(Dispatchers.Main)
        viewModel.searchRepo(query)

        btn.setOnClickListener {
            viewModel.searchRepo(query)
        }

        btn2.setOnClickListener {
            loadData()
        }
    }

    private fun loadData() {
        coroutineScope.launch(Dispatchers.Main) {
            loadFromNetWork()
        }

    }

    private suspend fun loadFromNetWork() {
        val query = "Android$IN_QUALIFIER"
        val service = GithubApiService.create()
        val database = RepoDatabase.getInstance(this@MainActivity)
        val dao = database.repoDao()
        val res = withContext(Dispatchers.IO){
            service.searchRepos(query, 1, 50).execute()
        }
        if (res.isSuccessful) {
                dao.insertReposss(res.body()!!.items)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, viewModel.lastQueryValue())
    }

    private fun initAdapter() {
        list.adapter = adapter
//        viewModel.reposOnly.observe(this, Observer<List<Repo>> {
//            showEmptyList(it?.size == 0)
//            adapter.submitList(it)
//        })
        viewModel.repos.observe(this, Observer{
            showEmptyList(it?.size == 0)
            adapter.submitList(it)
        })
        viewModel.networkErrors.observe(this, Observer<String> {
            Toast.makeText(this, "it", Toast.LENGTH_LONG).show()
        })

    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            emptyList.visibility = VISIBLE
            list.visibility = GONE
        } else {
            emptyList.visibility = GONE
            list.visibility = VISIBLE
        }
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "Android"
    }

}
