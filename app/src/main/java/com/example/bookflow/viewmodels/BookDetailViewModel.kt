package com.example.bookflow.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookflow.R
import com.example.bookflow.models.Book
import com.example.bookflow.repositories.BookRepository
import com.example.bookflow.repositories.LibraryRepository
import kotlinx.coroutines.launch

class BookDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "BookDetailViewModel"
    private val bookRepository = BookRepository()
    private val libraryRepository = LibraryRepository(application)

    private val _book = MutableLiveData<Book?>(null)
    val book: LiveData<Book?> = _book

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessageRes = MutableLiveData<Int?>(null)
    val errorMessageRes: LiveData<Int?> = _errorMessageRes

    private val _isSaved = MutableLiveData(false)
    val isSaved: LiveData<Boolean> = _isSaved

    fun loadBookDetails(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessageRes.value = null

            try {
                val bookDetails = bookRepository.getBookById(bookId)
                _book.value = bookDetails
                _isSaved.value = libraryRepository.isBookSaved(bookId)
                Log.d(tag, "Détails chargés pour : ${bookDetails.title}")
            } catch (exception: Exception) {
                _errorMessageRes.value = R.string.book_details_error
                Log.e(tag, "Erreur de chargement des détails", exception)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleSavedBook() {
        val currentBook = _book.value ?: return

        viewModelScope.launch {
            try {
                if (_isSaved.value == true) {
                    libraryRepository.deleteBook(currentBook.id)
                    _isSaved.value = false
                } else {
                    libraryRepository.saveBook(currentBook)
                    _isSaved.value = true
                }
            } catch (exception: Exception) {
                _errorMessageRes.value = R.string.library_update_error
                Log.e(tag, "Erreur lors de la modification de la bibliothèque", exception)
            }
        }
    }
}
