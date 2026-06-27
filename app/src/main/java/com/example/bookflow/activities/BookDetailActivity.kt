package com.example.bookflow.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bookflow.R
import com.example.bookflow.databinding.ActivityBookDetailBinding
import com.example.bookflow.models.Book
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
        // Configurer la toolbar avec un bouton retour
        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.detailToolbar.setNavigationOnClickListener {
            finish()
        }

        //Récuperer l'ID du livre depuis l'Intent
        val bookId = intent.getStringExtra(EXTRA_BOOK_ID)
        if (bookId.isNullOrBlank()) {
            Toast.makeText(this, "Erreur: aucun livre spécifié", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        observeViewModel()

        // Lancer le chargement
        viewModel.loadBookDetails(bookId)

    }

    private fun observeViewModel() {
        // Livre chargé
        viewModel.book.observe(this) { book ->
                book?.let { displayBook(it
                )}
        }




        viewModel.isLoading.observe(this) { isLoading ->
            binding.detailProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.detailScrollView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        // Erreur
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.detailErrorText.text = errorMessage
                binding.detailErrorText.visibility = View.VISIBLE
                binding.detailScrollView.visibility = View.GONE
            }

        }
    }


    // Rempli l'UI avec les infos du livre
    private fun displayBook(book: Book) {
        supportActionBar?.title = book.title

        // Couverture
        Glide.with(this)
            .load(book.coverUrl)
            .placeholder(R.drawable.ic_book_placeholder)
            .error(R.drawable.ic_book_placeholder)
            .into(binding.detailCoverImage)


        // Titre et auteur
        binding.detailTitle.text = book.title
        binding.detailAuthor.text = book.authors.joinToString(", ")

        val rating = if (book.averageRating != null) "⭐ ${book.averageRating}" else "Non noté"
        val pages = if (book.pageCount != null) "${book.pageCount} pages" else "Pages inconnues"
        binding.detailRatingPages.text = "$rating - $pages"


        binding.detailDescription.text = book.description?.takeIf { it.isNotBlank() }
            ?: "Aucune description disponible."


        binding.detailAddButton.setOnClickListener {
            // TODO : sera fait dans LibraryFragment plus tard
            Toast.makeText(this, " ${book.title} ajouté à votre bibliothèque", Toast.LENGTH_SHORT)
                .show()

        }
    }
}








