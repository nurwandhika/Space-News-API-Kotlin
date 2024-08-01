package com.dicoding.prdinewsapp.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.prdinewsapp.R
import com.dicoding.prdinewsapp.presentation.ui.adapters.NewsAdapter
import com.dicoding.prdinewsapp.viewmodels.RecentSearchViewModel

class RecentSearchActivity : AppCompatActivity() {
    private lateinit var recentRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var noRecentArticlesTextView: TextView
    private val recentSearchViewModel: RecentSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_search)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Recent Search"

        toolbar.setNavigationOnClickListener {
            finish()
        }

        recentRecyclerView = findViewById(R.id.recentRecyclerView)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        noRecentArticlesTextView = findViewById(R.id.noRecentArticlesTextView)
        recentRecyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(listOf())
        recentRecyclerView.adapter = newsAdapter

        recentSearchViewModel.recentArticles.observe(this, Observer { articles ->
            newsAdapter.updateArticles(articles)
            showLoading(false)
            noRecentArticlesTextView.visibility = if (articles.isEmpty()) View.VISIBLE else View.GONE
        })

        showLoading(true)
    }

    private fun showLoading(isLoading: Boolean) {
        loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}