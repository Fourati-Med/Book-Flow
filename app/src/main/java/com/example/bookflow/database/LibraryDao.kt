package com.example.bookflow.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LibraryDao {


     //Récupère tous les livres sauvegardés, triés du plus récent au plus ancien.

    @Query("SELECT * FROM saved_books ORDER BY savedAt DESC")
    fun getAllSavedBooks(): LiveData<List<SavedBookEntity>>


     // Insère un livre. Si le livre existe déjà (même id), il est remplacé.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: SavedBookEntity)


     // Supprime un livre par son ID.

    @Query("DELETE FROM saved_books WHERE id = :bookId")
    suspend fun deleteBook(bookId: String)


     //Vérifie si un livre est déjà sauvegardé.
      //Retourne 0 si non, 1 si oui.

    @Query("SELECT COUNT(*) FROM saved_books WHERE id = :bookId")
    suspend fun isBookSaved(bookId: String): Int
}