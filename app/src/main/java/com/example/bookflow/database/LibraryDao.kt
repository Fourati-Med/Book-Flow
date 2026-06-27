package com.example.bookflow.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface LibraryDao {

    // Récupère tous les livres sauvegardés, triés du plus récent au plus ancien.
    @Query("SELECT * FROM saved_books ORDER BY savedAt DESC")
    fun getAllSavedBooks(): LiveData<List<SavedBookEntity>>



    @Insert(onConflict = onConflictStrategy.REPLACE)
    suspend fun insertBook(book: SavedBookEntity)

    @Query("DELETE FROM saved_books WHERE id = :bookId")
    suspend fun deleteBook(bookId: String)

    @Query("SELECT COUNT(*) FROM saved_books WHERE id = :bookId")
    suspend fun isBookSaved(bookId: String): Int
}