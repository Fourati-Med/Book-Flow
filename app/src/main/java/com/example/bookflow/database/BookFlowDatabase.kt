package com.example.bookflow.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room

//Base de données locale

@Database(
    entities = [SavedBookEntity::class],
    version= 1,
    exportSchema = false
)
abstract class BookFlowDatabase : RoomDatabase(){
    abstract fun libraryDao(): LibraryDao

    companion object{
        @Volatile
        private var INSTANCE: BookFlowDatabase? = null

        fun getInstance(context: Context): BookFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookFlowDatabase::class.java,
                    "bookflow_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}