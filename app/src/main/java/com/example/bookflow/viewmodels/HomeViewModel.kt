package com.example.bookflow.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookflow.models.Book
import com.example.bookflow.repositories.BookRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val tag = "HomeViewModel"
    private val repository = BookRepository()

    // Liste des livres : MutableLiveData privé, LiveData public en lecture seule
    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    // Indicateur de chargement (true = afficher ProgressBar)
     private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Message d'erreur (null = pas d'erreur)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init{
        loadBooks()
    }

    fun loadBooks(){
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val books = repository.searchBooks(query = "Harry Potter", maxResults = 20)
                _books.value = books
                Log.d(tag, "Chargement réussi : ${books.size} livres récupérés")

        }    catch (e: Exception) {
            _errorMessage.value = "Impossible de charger les livres. Vérifiez votre connexion."
            Log.e(tag,"Error lors du chargement des livres.", e)
        } finally {
            _isLoading.value = false
        }
      }
    }
}