package com.example.bookflow.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity transforme cette classe en table SQLite

@Entity(tableName = "saved_books")
data class SavedBookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val authors: String,
    val coverUrl: String?,
    val description: String?,
    val pageCount: Int?,
    val averageRating: Double?,
    val savedAt: Long = System.currentTimeMillis()

)