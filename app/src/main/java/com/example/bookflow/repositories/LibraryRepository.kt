package com.example.bookflow.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.bookflow.database.BookFlowDatabase
import com.example.bookflow.database.SavedBookEntity
import com.example.bookflow.models.Book

// Pour gérer les livres sauvegardés en local
class LibraryRepository (context: Context) {
    private val libraryDao = BookFlowDatabase.getInstance(context).libraryDao()

    fun getAllSavedBooks(): LiveData<List<Book>> {
        return libraryDao.getAllSavedBooks().map { entities ->
            entities.map { it.toBook() }

        }
    }

    // Ajoute un livre à la bibliothéque

    suspend fun saveBook(book: Book) {
        libraryDao.insertBook(book.toEntity())
    }

    suspend fun deleteBook(bookId: String) {
        libraryDao.deleteBook(bookId)
    }

    suspend fun isBookSaved(bookId: String): Boolean {
        return libraryDao.isBookSaved(bookId) > 0
    }

    // Conversion entity -> book

    private fun SavedBookEntity.toBook(): Book {
        return Book(
            id = id,
            title = title,
            authors = authors.split(", "),
            coverUrl = coverUrl,
            description = description,
            pageCount = pageCount,
            averageRating = averageRating
        )
    }

    // Conversion book -> entity
    private fun Book.toEntity(): SavedBookEntity {
        return SavedBookEntity(
            id = id,
            title = title,
            authors = authors.joinToString(", "),
            coverUrl = coverUrl,
            description = description,
            pageCount = pageCount,
            averageRating = averageRating
        )


    }
}