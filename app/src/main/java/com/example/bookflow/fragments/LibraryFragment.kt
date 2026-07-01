package com.example.bookflow.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookflow.activities.BookDetailActivity
import com.example.bookflow.adapters.BookAdapter
import com.example.bookflow.databinding.FragmentLibraryBinding
import com.example.bookflow.models.Book
import com.example.bookflow.viewmodels.LibraryViewModel

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModels()
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
        setupRecyclerView()
        observeLibrary()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(::openBookDetails)
        binding.libraryRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }

    private fun observeLibrary() {
        viewModel.books.observe(viewLifecycleOwner) { books ->
            val savedBooks = books.orEmpty()
            bookAdapter.submitList(savedBooks)

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
