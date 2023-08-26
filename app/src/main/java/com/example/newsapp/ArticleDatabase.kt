package com.example.newsapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Article::class], version = 2)
abstract class ArticleDatabase: RoomDatabase() {
    abstract fun articleDao() : ArticleDao

    companion object{


        @Volatile
        private var INSTANCE: ArticleDatabase? = null

        fun getDatabase(context: Context):ArticleDatabase{
            if(INSTANCE == null){
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ArticleDatabase::class.java,
                        "articleDb").build()
                }
            }
            return INSTANCE!!
        }
    }
}