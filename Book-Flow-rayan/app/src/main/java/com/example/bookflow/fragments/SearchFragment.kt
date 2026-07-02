package com.example.bookflow.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookflow.R
import com.example.bookflow.activities.BookDetailActivity
import com.example.bookflow.adapters.BookAdapter
import com.example.bookflow.databinding.FragmentSearchBinding
import com.example.bookflow.models.Book
import com.example.bookflow.viewmodels.SearchViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var bookAdapter: BookAdapter

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
        observeViewModel()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(::onBookClicked)
        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookAdapter
        }
    }

    private fun setupSearchInput() {
        binding.searchEditText.addTextChangedListener { text ->
            viewModel.search(text?.toString().orEmpty())
        }
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { books ->
            bookAdapter.submitList(books)

            if (books.isEmpty() && viewModel.errorMessageRes.value == null) {
                binding.searchRecyclerView.visibility = View.GONE
                binding.searchInfoText.visibility = View.VISIBLE
                val currentQuery = binding.searchEditText.text?.toString().orEmpty()
                binding.searchInfoText.text = if (currentQuery.isBlank()) {
                    getString(R.string.search_prompt)
                } else {
                    getString(R.string.search_no_result, currentQuery)
                }
            } else if (books.isNotEmpty()) {
                binding.searchRecyclerView.visibility = View.VISIBLE
                binding.searchInfoText.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.searchProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessageRes.observe(viewLifecycleOwner) { errorMessageRes ->
            if (errorMessageRes != null) {
                binding.searchInfoText.setText(errorMessageRes)
                binding.searchInfoText.visibility = View.VISIBLE
                binding.searchRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun onBookClicked(book: Book) {
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

private fun com.google.android.material.textfield.TextInputEditText.addTextChangedListener(
    onTextChanged: (CharSequence?) -> Unit
) {
    addTextChangedListener(object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s)
        }
        override fun afterTextChanged(s: android.text.Editable?) = Unit
    })
}
