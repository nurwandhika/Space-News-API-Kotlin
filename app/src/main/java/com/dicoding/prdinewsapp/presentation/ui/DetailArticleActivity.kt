package com.dicoding.prdinewsapp.presentation.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.IntentCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.prdinewsapp.R
import com.dicoding.prdinewsapp.data.db.AppDatabase
import com.dicoding.prdinewsapp.data.models.Article
import com.dicoding.prdinewsapp.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailArticleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_article)

        val article =
            IntentCompat.getParcelableExtra(intent, "article", Article::class.java) as Article
        saveArticleToRecent(article)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Artikel"

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val articleTitle: TextView = findViewById(R.id.articleTitle)
        val articleSource: TextView = findViewById(R.id.articleSource)
        val articleDescription: TextView = findViewById(R.id.articleDescription)
        val articleDateTime: TextView = findViewById(R.id.articleDateTime)
        val articleImage: ImageView = findViewById(R.id.articleImage)

        articleTitle.text = article.title
        articleSource.text = article.news_site
        articleDescription.text = extractFirstSentence(article.summary)
        articleDateTime.text = DateUtils.formatDateTime(article.published_at)
        Glide.with(this)
            .load(article.image_url)
            .apply(RequestOptions().placeholder(R.drawable.loading_placeholder))
            .into(articleImage)
    }

    private fun saveArticleToRecent(article: Article) {
        CoroutineScope(Dispatchers.IO).launch {
            val articleDao = AppDatabase.getDatabase(applicationContext).articleDao()
            articleDao.upsert(article)
        }
    }

    private fun extractFirstSentence(summary: String): String {
        return summary.split(".")[0] + "."
    }
}