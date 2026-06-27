package com.example.bookflow.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookflow.models.Book
import com.example.bookflow.repositories.BookRepository
import kotlinx.coroutines.launch

class BookDetailViewModel : ViewModel() {
    private val tag = "BookDetailViewModel"
    private val repository = BookRepository()

    private val _book = MutableLiveData<Book?>(null)
    val book: LiveData<Book?> = _book

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

fun loadBookDetails(bookId: String) {
    viewModelScope.launch {
        _isLoading.value = true
        _errorMessage.value = null

        try {
            val bookDetails = repository.getBookById(bookId)
            _book.value = bookDetails
            Log.d(tag, " Détails chargés pour : ${bookDetails?.title}")
        } catch (e: Exception) {
            _errorMessage.value = "Impossible de charger les détails du livre."
            Log.e(tag, "Erreur de chargement des détails", e)
        } finally {
            _isLoading.value = false
        }
    }
}

}