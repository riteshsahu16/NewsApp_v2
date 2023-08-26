package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.contentValuesOf
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide

class NewsAdapter(val context: Context, val articles: List<Article>):
    RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>(){

    class ArticleViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
        var newsImage = itemview.findViewById<ImageView>(R.id.newImage)
        var rvTitle = itemview.findViewById<TextView>(R.id.tvTitle)
        var rvDescription = itemview.findViewById<TextView>(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        var layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.item_view, parent, false)
        return ArticleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        //holder.newsImage.setImageURI(articles[position].urlToImage.toUri())
        Glide.with(context).load(articles[position].urlToImage).into(holder.newsImage)
        holder.rvTitle.text = articles[position].title
        holder.rvDescription.text = articles[position].description

        holder.itemView.setOnClickListener{
            val intent = Intent(context, DetailActity::class.java)
            intent.putExtra("url", articles[position].url)
            context.startActivity(intent)
        }
    }
}