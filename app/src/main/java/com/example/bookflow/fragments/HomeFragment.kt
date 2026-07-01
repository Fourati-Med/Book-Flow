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
import com.example.bookflow.databinding.FragmentHomeBinding
import com.example.bookflow.models.Book
import com.example.bookflow.repositories.BookRepository
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private val tag = "HomeFragment"

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val repository = BookRepository()
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadBooks()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(
            onBookClick = { book -> onBookClicked(book) }
        )
        binding.recyclerViewBooks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }


     //Charge les livres depuis l'API Google Books via le Repository.

    private fun loadBooks() {
        lifecycleScope.launch {
            // Afficher le loading
            binding.progressBar.visibility = View.VISIBLE
            binding.textViewError.visibility = View.GONE
            binding.recyclerViewBooks.visibility = View.GONE

            try {
                val books = repository.searchBooks(query = "Harry Potter", maxResults = 20)
                Log.d(tag, "Chargement réussi : ${books.size} livres")

                bookAdapter.updateBooks(books)
                binding.recyclerViewBooks.visibility = View.VISIBLE

            } catch (e: Exception) {
                Log.e(tag, "Erreur de chargement", e)
                binding.textViewError.text = "Impossible de charger les livres."
                binding.textViewError.visibility = View.VISIBLE

            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun onBookClicked(book: Book) {
        Log.d(tag, "Livre cliqué : ${book.title}")
        val intent = android.content.Intent(requireContext(), BookDetailActivity::class.java)
        intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, book.id)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}