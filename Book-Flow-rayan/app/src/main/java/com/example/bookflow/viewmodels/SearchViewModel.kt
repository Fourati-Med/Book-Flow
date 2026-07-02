package com.example.bookflow.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookflow.R
import com.example.bookflow.models.Book
import com.example.bookflow.repositories.BookRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val tag = "SearchViewModel"
    private val repository = BookRepository()

    private val _searchResults = MutableLiveData<List<Book>>(emptyList())
    val searchResults: LiveData<List<Book>> = _searchResults

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessageRes = MutableLiveData<Int?>(null)
    val errorMessageRes: LiveData<Int?> = _errorMessageRes

    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isLoading.value = false
            _errorMessageRes.value = null
            return
        }

        searchJob = viewModelScope.launch {
            delay(500)
            _isLoading.value = true
            _errorMessageRes.value = null

            try {
                val books = repository.searchBooks(query = query, maxResults = 20)
                _searchResults.value = books
                Log.d(tag, "Recherche '$query' : ${books.size} résultats")
            } catch (exception: Exception) {
                _errorMessageRes.value = R.string.search_error
                _searchResults.value = emptyList()
                Log.e(tag, "Erreur lors de la recherche de '$query'", exception)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
