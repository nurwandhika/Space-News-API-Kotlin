package com.dicoding.prdinewsapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.prdinewsapp.R
import com.dicoding.prdinewsapp.data.api.RetrofitInstance
import com.dicoding.prdinewsapp.data.models.Article
import com.dicoding.prdinewsapp.presentation.ui.adapters.NewsAdapter
import com.dicoding.prdinewsapp.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsActivity : AppCompatActivity() {
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var searchCardView: CardView
    private lateinit var filterSpinner: Spinner
    private lateinit var noArticlesTextView: TextView
    private var articles: MutableList<Article> = mutableListOf()
    private var currentFilter: String = "All"
    private var currentQuery: String = ""
    private var isLoading = false
    private var currentPage = 1
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        newsRecyclerView = findViewById(R.id.newsRecyclerView)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        searchView = findViewById(R.id.searchView)
        searchCardView = findViewById(R.id.searchCardView)
        filterSpinner = findViewById(R.id.filterSpinner)
        noArticlesTextView = findViewById(R.id.noArticlesTextView)
        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter(articles)
        newsRecyclerView.adapter = newsAdapter

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentFilter = filterSpinner.selectedItem as String
                filterArticles()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        loadArticles()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(Constants.SEARCH_NEWS_TIME_DELAY)
                    currentQuery = newText ?: ""
                    filterArticles()
                }
                return true
            }
        })

        searchCardView.setOnClickListener {
            searchView.isIconified = false
            searchView.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_history -> {
                startActivity(Intent(this, RecentSearchActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadArticles() {
        isLoading = true
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.getArticles(
                page = currentPage,
                pageSize = 10
            )
            if (response.isSuccessful) {
                response.body()?.let { newsResponse ->
                    articles.addAll(newsResponse.results)
                    val newsSites = articles.map { it.news_site }.distinct().toMutableList()
                    newsSites.add(0, "All")
                    withContext(Dispatchers.Main) {
                        val adapter =
                            ArrayAdapter(this@NewsActivity, R.layout.spinner_item, newsSites)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        filterSpinner.adapter = adapter
                        newsAdapter.updateArticles(articles)
                        isLoading = false
                        showLoading(false)
                        currentPage++
                    }
                }
            }
        }
    }

    private fun filterArticles() {
        val filteredArticles = articles.filter {
            (currentFilter == "All" || it.news_site == currentFilter) &&
                    it.title.contains(currentQuery, ignoreCase = true)
        }
        newsAdapter.updateArticles(filteredArticles)
        noArticlesTextView.visibility = if (filteredArticles.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}