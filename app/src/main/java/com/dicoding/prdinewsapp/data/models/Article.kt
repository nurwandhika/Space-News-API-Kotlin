package com.dicoding.prdinewsapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val news_site: String,
    val summary: String,
    val published_at: String,
    val image_url: String
) : Serializable