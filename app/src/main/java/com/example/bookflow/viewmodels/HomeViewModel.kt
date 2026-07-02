package com.example.bookflow.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookflow.R
import com.example.bookflow.models.Book
import com.example.bookflow.repositories.BookRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val tag = "HomeViewModel"
    private val repository = BookRepository()

    private val _books = MutableLiveData<List<Book>>(emptyList())
    val books: LiveData<List<Book>> = _books

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessageRes = MutableLiveData<Int?>(null)
    val errorMessageRes: LiveData<Int?> = _errorMessageRes

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessageRes.value = null

            try {
                val books = repository.searchBooks(query = "Harry Potter", maxResults = 20)
                _books.value = books
                Log.d(tag, "Chargement réussi : ${books.size} livres récupérés")
            } catch (exception: Exception) {
                _errorMessageRes.value = R.string.load_books_error
                Log.e(tag, "Erreur lors du chargement des livres", exception)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
