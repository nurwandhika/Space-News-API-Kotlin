package com.dicoding.prdinewsapp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.dicoding.prdinewsapp.data.db.AppDatabase
import com.dicoding.prdinewsapp.data.models.Article

class RecentSearchViewModel(application: Application) : AndroidViewModel(application) {
    private val articleDao = AppDatabase.getDatabase(application).articleDao()
    val recentArticles: LiveData<List<Article>> = articleDao.getAllArticles()
}