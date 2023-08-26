package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: NewsAdapter
    private lateinit var rvNews: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var articles = mutableListOf<Article>()
    private var pageNum = 1
    private var totalResult = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val articleDao = ArticleDatabase.getDatabase(applicationContext).articleDao()
        val networkConnection = NetworkConnection(applicationContext)

        rvNews = findViewById(R.id.rvNews)
        progressBar = findViewById(R.id.progressMain)

        adapter = NewsAdapter(this, articles)
        rvNews.adapter = adapter
        rvNews.layoutManager = LinearLayoutManager(this)


        rvNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val visibleItemCount =
                        (rvNews.layoutManager as LinearLayoutManager).childCount
                    val pastVisibleItem =
                        (rvNews.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                    val total = (rvNews.layoutManager as LinearLayoutManager).itemCount
                    if (visibleItemCount + pastVisibleItem >= total) {
                        pageNum++
                        val news = NewsService.newsInstance.getHeadlines("in", pageNum)
                        news.enqueue(object : Callback<Result> {
                            override fun onResponse(
                                call: Call<Result>,
                                response: Response<Result>
                            ) {
                                val news = response.body()
                                progressBar.visibility = View.GONE
                                rvNews.visibility = View.VISIBLE
                                var article_count: Int
                                if (news != null) {
                                    runBlocking {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Saving Data",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        articleDao.saveArticles(news.articles)
                                        article_count = articleDao.getArticles().size
                                    }
                                    totalResult = news.totalResult
                                    articles.addAll(news.articles)
                                    adapter.notifyDataSetChanged()
                                }
                            }

                            override fun onFailure(call: Call<Result>, t: Throwable) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error fetching news",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })

                    }

                    super.onScrolled(recyclerView, dx, dy)
                }
            }

        })


        val disconnected = findViewById<LinearLayout>(R.id.tvDisconnected)

        networkConnection.observe(this@MainActivity) {
            if (it) {
                Toast.makeText(this@MainActivity, "Connected to Internet", Toast.LENGTH_LONG)
                    .show()
                disconnected.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                val news = NewsService.newsInstance.getHeadlines("in", pageNum)
                news.enqueue(object : Callback<Result> {
                    override fun onResponse(call: Call<Result>, response: Response<Result>) {
                        val news = response.body()
                        progressBar.visibility = View.GONE
                        rvNews.visibility = View.VISIBLE
                        var article_count: Int
                        if (news != null) {
                            runBlocking {
                                Toast.makeText(this@MainActivity, "Saving Data", Toast.LENGTH_LONG)
                                    .show()
                                articleDao.saveArticles(news.articles)
                                article_count = articleDao.getArticles().size
                            }
                            Toast.makeText(this@MainActivity, "Saved $article_count Data", Toast.LENGTH_LONG)

                            totalResult = news.totalResult
                            articles.addAll(news.articles)
                            adapter.notifyDataSetChanged()
                        }
                    }

                    override fun onFailure(call: Call<Result>, t: Throwable) {
                        Toast.makeText(
                            this@MainActivity,
                            "Error fetching news",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                runBlocking {
                    articles = articleDao.getArticles().toMutableList()
                }
                adapter = NewsAdapter(this, articles)
                rvNews.adapter = adapter
                Toast.makeText(
                    this@MainActivity,
                    "DisConnected: Offline Records ${articles.size}",
                    Toast.LENGTH_SHORT
                ).show()

                Log.d("MainActivity", articles.toString())
                rvNews.visibility = View.VISIBLE
                disconnected.visibility = View.VISIBLE
                progressBar.visibility = View.GONE

            }

        }
    }

}