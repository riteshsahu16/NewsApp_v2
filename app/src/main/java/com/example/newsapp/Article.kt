package com.example.newsapp

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName =  "tbl_article")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val article_id:Int,
    val author:String?,
    val title:String,
    val description:String?,
    val url:String?,
    val urlToImage:String?)
