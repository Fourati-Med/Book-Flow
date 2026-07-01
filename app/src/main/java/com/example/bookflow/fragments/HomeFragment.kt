package com.example.bookflow.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookflow.activities.BookDetailActivity
import com.example.bookflow.adapters.BookAdapter
import com.example.bookflow.databinding.FragmentHomeBinding
import com.example.bookflow.models.Book
import com.example.bookflow.viewmodels.HomeViewModel

class HomeFragment : Fragment() {

    private val tag = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
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
        observeViewModel()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(::onBookClicked)
        binding.recyclerViewBooks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.books.observe(viewLifecycleOwner) { books ->
            Log.d(tag, "Mise à jour UI : ${books.size} livres reçus")
            bookAdapter.submitList(books)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessageRes.observe(viewLifecycleOwner) { errorMessageRes ->
            if (errorMessageRes != null) {
                binding.textViewError.setText(errorMessageRes)
                binding.textViewError.visibility = View.VISIBLE
                binding.recyclerViewBooks.visibility = View.GONE
            } else {
                binding.textViewError.visibility = View.GONE
                binding.recyclerViewBooks.visibility = View.VISIBLE
            }
        }
    }

    private fun onBookClicked(book: Book) {
        Log.d(tag, "Livre cliqué : ${book.title}")
        startActivity(
            Intent(requireContext(), BookDetailActivity::class.java).apply {
                putExtra(BookDetailActivity.EXTRA_BOOK_ID, book.id)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
