package com.example.bookflow.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.bookflow.R
import com.example.bookflow.databinding.ActivityBookDetailBinding
import com.example.bookflow.models.Book
import com.example.bookflow.utils.SystemBarInsets
import com.example.bookflow.viewmodels.BookDetailViewModel

class BookDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id"
    }

    private lateinit var binding: ActivityBookDetailBinding
    private val viewModel: BookDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        SystemBarInsets.apply(this, binding.detailToolbar, binding.detailScrollView)
        observeViewModel()

        val bookId = intent.getStringExtra(EXTRA_BOOK_ID)
        if (bookId.isNullOrBlank()) {
            Toast.makeText(this, R.string.error_no_book, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel.loadBookDetails(bookId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.detailToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        viewModel.book.observe(this) { book ->
            book?.let(::displayBook)
        }

        viewModel.isSaved.observe(this) { isSaved ->
            binding.detailAddButton.setText(
                if (isSaved) R.string.remove_from_library else R.string.add_to_library
            )
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.detailProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (viewModel.errorMessageRes.value == null) {
                binding.detailScrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        }

        viewModel.errorMessageRes.observe(this) { errorMessageRes ->
            if (errorMessageRes == null) {
                binding.detailErrorText.visibility = View.GONE
            } else {
                binding.detailErrorText.setText(errorMessageRes)
                binding.detailErrorText.visibility = View.VISIBLE
                binding.detailScrollView.visibility = View.GONE
            }
        }
    }

    private fun displayBook(book: Book) {
        supportActionBar?.title = book.title.ifBlank { getString(R.string.unknown_title) }

        Glide.with(this)
            .load(book.coverUrl)
            .placeholder(R.drawable.ic_book_placeholder)
            .error(R.drawable.ic_book_placeholder)
            .into(binding.detailCoverImage)

        binding.detailTitle.text = book.title.ifBlank { getString(R.string.unknown_title) }
        binding.detailAuthor.text = book.getAuthorsAsString()
            .ifBlank { getString(R.string.unknown_author) }

        val rating = book.averageRating?.let { getString(R.string.rating_value, it) }
            ?: getString(R.string.not_rated)
        val pages = book.pageCount?.let { getString(R.string.page_count, it) }
            ?: getString(R.string.pages_unknown)
        binding.detailRatingPages.text = getString(R.string.rating_and_pages, rating, pages)

        val description = book.description?.takeIf { it.isNotBlank() }
        binding.detailDescription.text = if (description == null) {
            getString(R.string.no_description)
        } else {
            HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }

        binding.detailAddButton.setOnClickListener {
            viewModel.toggleSavedBook()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
