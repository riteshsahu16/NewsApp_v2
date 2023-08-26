package com.example.newsapp

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.google.android.material.circularreveal.CircularRevealHelper.Strategy

@Dao
interface ArticleDao {
    @Upsert()
    suspend fun saveArticles(articles: List<Article>)

    @Query("SELECT * FROM tbl_article")
    suspend fun getArticles():List<Article>
}