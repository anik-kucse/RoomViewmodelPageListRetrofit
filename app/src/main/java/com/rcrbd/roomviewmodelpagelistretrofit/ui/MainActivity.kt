package com.rcrbd.roomviewmodelpagelistretrofit.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rcrbd.roomviewmodelpagelistretrofit.Injection
import com.rcrbd.roomviewmodelpagelistretrofit.R
import com.rcrbd.roomviewmodelpagelistretrofit.api.DEFAULT_QUERY
import com.rcrbd.roomviewmodelpagelistretrofit.api.GithubApiService
import com.rcrbd.roomviewmodelpagelistretrofit.api.IN_QUALIFIER
import com.rcrbd.roomviewmodelpagelistretrofit.api.ServerResponse
import com.rcrbd.roomviewmodelpagelistretrofit.database.RepoDatabase
import com.rcrbd.roomviewmodelpagelistretrofit.model.Repo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: RepoViewModel
    private val adapter = ReposAdapter()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(this)).get(RepoViewModel::class.java)

        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        list.addItemDecoration(decoration)
        setupScrollListener()

        initAdapter()

        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        viewModel.searchRepo(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, viewModel.lastQueryValue())
    }

    private fun initAdapter() {
        list.adapter = adapter
        viewModel.repos.observe(this, Observer<List<Repo>>{
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

    private fun setupScrollListener() {
        val layoutManager = list.layoutManager as LinearLayoutManager
        list.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                viewModel.listScrolled(visibleItemCount, lastVisibleItemPosition, totalItemCount)
            }
        } )
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "Android"
    }

}
