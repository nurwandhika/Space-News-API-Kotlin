package com.dicoding.prdinewsapp.data.models

data class NewsResponse(
    val count: Int,
    val next: String?,
    val previous: Any?,
    val results: MutableList<Article>
)