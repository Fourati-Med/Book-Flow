package com.example.bookflow.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookflow.models.Book
import com.example.bookflow.repositories.BookRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val tag = "SearchViewModel"
    private val repository = BookRepository()
    private val _searchResults = MutableLiveData<List<Book>>()
    val searchResults: LiveData<List<Book>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    // Job actuel : permet d'annuler la recherche précédente si une nouvelle arrive
    private var searchJob: Job? = null


    fun search(query: String) {
        //Annule la recherche en cours s'il y en a une
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResults.value = emptyList()
            _isLoading.value = false
            return
        }

        searchJob = viewModelScope.launch {
            // Debounce : on attend 500ms avant de lancer
            delay(500)

            _isLoading.value = true
            _errorMessage.value = null

            try {
                val books = repository.searchBooks(query = query, maxResults = 20)
                _searchResults.value = books
                Log.d(tag, "Recherche '$query' : ${books.size} résultats")

            } catch (e: Exception) {
                _errorMessage.value = "Erreur de recherche. Réssayez."
                Log.e(tag, "Erreur lors de la recherche de '$query'", e)
            } finally {
                _isLoading.value = false
            }


        }


    }


}