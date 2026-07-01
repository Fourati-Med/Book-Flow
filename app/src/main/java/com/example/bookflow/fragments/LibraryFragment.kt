package com.example.bookflow.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookflow.activities.BookDetailActivity
import com.example.bookflow.adapters.BookAdapter
import com.example.bookflow.databinding.FragmentLibraryBinding
import com.example.bookflow.models.Book
import com.example.bookflow.repositories.LibraryRepository


class LibraryFragment : Fragment() {

    private val tag = "LibraryFragment"

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var libraryRepository: LibraryRepository
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Créer le Repository (nécessite un Context)
        libraryRepository = LibraryRepository(requireContext())

        setupRecyclerView()
        observeLibrary()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(
            onBookClick = { book -> openBookDetails(book) }
        )
        binding.libraryRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }


     // Se met à jour automatiquement à chaque changement en BDD.

    private fun observeLibrary() {
        libraryRepository.getAllSavedBooks().observe(viewLifecycleOwner) { books ->
            val savedBooks = books.orEmpty()
            Log.d(tag, "Bibliothèque mise à jour : ${savedBooks.size} livres")

            bookAdapter.updateBooks(savedBooks)

            // Afficher message si vide
            val isEmpty = savedBooks.isEmpty()
            binding.libraryEmptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.libraryRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }
    }

    private fun openBookDetails(book: Book) {
        val intent = Intent(requireContext(), BookDetailActivity::class.java).apply {
            putExtra(BookDetailActivity.EXTRA_BOOK_ID, book.id)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}