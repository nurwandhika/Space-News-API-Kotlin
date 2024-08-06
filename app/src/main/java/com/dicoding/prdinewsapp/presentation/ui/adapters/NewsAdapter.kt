package com.dicoding.prdinewsapp.presentation.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.dicoding.prdinewsapp.R
import com.dicoding.prdinewsapp.data.models.Article
import com.dicoding.prdinewsapp.presentation.ui.DetailArticleActivity
import com.dicoding.prdinewsapp.utils.DateUtils

class NewsAdapter(private var articles: List<Article>) :
    RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    fun updateArticles(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val articleTitle: TextView = itemView.findViewById(R.id.articleTitle)
        private val articleSource: TextView = itemView.findViewById(R.id.articleSource)
        private val articleDescription: TextView = itemView.findViewById(R.id.articleDescription)
        private val articleDateTime: TextView = itemView.findViewById(R.id.articleDateTime)
        private val articleImage: ImageView = itemView.findViewById(R.id.articleImage)
        private val loadingIndicator: ProgressBar = itemView.findViewById(R.id.loadingIndicator)

        fun bind(article: Article) {
            articleTitle.text = article.title
            articleSource.text = article.news_site
            articleDescription.text = article.summary
            articleDateTime.text = DateUtils.formatDateTime(article.published_at)

            loadingIndicator.visibility = View.VISIBLE
            Glide.with(itemView.context)
                .load(article.image_url)
                .apply(RequestOptions().override(Target.SIZE_ORIGINAL))
                .into(object : com.bumptech.glide.request.target.CustomTarget<android.graphics.drawable.Drawable>() {
                    override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                        loadingIndicator.visibility = View.GONE
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable,
                        transition: Transition<in android.graphics.drawable.Drawable>?
                    ) {
                        articleImage.setImageDrawable(resource)
                        loadingIndicator.visibility = View.GONE
                    }
                })

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailArticleActivity::class.java)
                intent.putExtra("article", article)
                itemView.context.startActivity(intent)
            }
        }
    }
}