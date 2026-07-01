package com.example.bookflow.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookflow.activities.BookDetailActivity
import com.example.bookflow.adapters.BookAdapter
import com.example.bookflow.databinding.FragmentSearchBinding
import com.example.bookflow.models.Book
import com.example.bookflow.repositories.BookRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private val tag = "SearchFragment"

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val repository = BookRepository()
    private lateinit var bookAdapter: BookAdapter

    // Job de la recherche courante (pour l'annuler si nouvelle recherche)
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchInput()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(
            onBookClick = { book -> onBookClicked(book) }
        )
        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }

    private fun setupSearchInput() {
        binding.searchEditText.addTextChangedListener { text ->
            performSearch(text?.toString() ?: "")
        }
    }


    private fun performSearch(query: String) {
        // Annuler la recherche précédente
        searchJob?.cancel()

        // Si query vide, on vide les résultats
        if (query.isBlank()) {
            bookAdapter.updateBooks(emptyList())
            binding.searchRecyclerView.visibility = View.GONE
            binding.searchInfoText.text = "Recherchez un livre par titre, auteur ou ISBN"
            binding.searchInfoText.visibility = View.VISIBLE
            return
        }

        searchJob = lifecycleScope.launch {
            // Debounce : on attend 500ms
            delay(500)

            binding.searchProgressBar.visibility = View.VISIBLE

            try {
                val books = repository.searchBooks(query = query, maxResults = 20)
                Log.d(tag, "Recherche '$query' : ${books.size} résultats")

                bookAdapter.updateBooks(books)

                if (books.isEmpty()) {
                    binding.searchRecyclerView.visibility = View.GONE
                    binding.searchInfoText.text = "Aucun résultat pour \"$query\""
                    binding.searchInfoText.visibility = View.VISIBLE
                } else {
                    binding.searchRecyclerView.visibility = View.VISIBLE
                    binding.searchInfoText.visibility = View.GONE
                }

            } catch (e: Exception) {
                Log.e(tag, "Erreur de recherche", e)
                binding.searchInfoText.text = "Erreur de recherche. Réessayez."
                binding.searchInfoText.visibility = View.VISIBLE
                binding.searchRecyclerView.visibility = View.GONE

            } finally {
                binding.searchProgressBar.visibility = View.GONE
            }
        }
    }

    private fun onBookClicked(book: Book) {
        val intent = android.content.Intent(requireContext(), BookDetailActivity::class.java)
        intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, book.id)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


private fun com.google.android.material.textfield.TextInputEditText.addTextChangedListener(
    onTextChanged: (CharSequence?) -> Unit
) {
    addTextChangedListener(object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s)
        }
        override fun afterTextChanged(s: android.text.Editable?) {}
    })
}