package com.example.bookflow.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.bookflow.models.Book
import com.example.bookflow.repositories.LibraryRepository

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LibraryRepository(application)

    val books: LiveData<List<Book>> = repository.getAllSavedBooks()
}
