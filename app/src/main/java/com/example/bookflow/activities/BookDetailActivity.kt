package com.example.bookflow.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.bookflow.R
import com.example.bookflow.databinding.ActivityBookDetailBinding
import com.example.bookflow.models.Book
import com.example.bookflow.repositories.BookRepository
import com.example.bookflow.repositories.LibraryRepository
import kotlinx.coroutines.launch

class BookDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_BOOK_ID = "extra_book_id"
    }

    private val tag = "BookDetailActivity"

    private lateinit var binding: ActivityBookDetailBinding
    private val repository = BookRepository()
    private val libraryRepository by lazy { LibraryRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar avec bouton retour
        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.detailToolbar.setNavigationOnClickListener {
            finish()
        }

        // Récupérer l'ID du livre depuis l'Intent
        val bookId = intent.getStringExtra(EXTRA_BOOK_ID)
        if (bookId.isNullOrBlank()) {
            Toast.makeText(this, "Erreur : aucun livre spécifié", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadBookDetails(bookId)
    }


    //Charge les détails du livre via le Repository.

    private fun loadBookDetails(bookId: String) {
        lifecycleScope.launch {
            binding.detailProgressBar.visibility = View.VISIBLE
            binding.detailScrollView.visibility = View.GONE
            binding.detailErrorText.visibility = View.GONE

            try {
                val book = repository.getBookById(bookId)
                if (book != null) {
                    displayBook(book)
                    binding.detailScrollView.visibility = View.VISIBLE
                } else {
                    showError("Livre introuvable")
                }
                Log.d(tag, "Détails chargés : ${book?.title}")

            } catch (e: Exception) {
                Log.e(tag, "Erreur de chargement", e)
                showError("Impossible de charger les détails.")

            } finally {
                binding.detailProgressBar.visibility = View.GONE
            }
        }
    }

    private fun showError(message: String) {
        binding.detailErrorText.text = message
        binding.detailErrorText.visibility = View.VISIBLE
        binding.detailScrollView.visibility = View.GONE
    }


    //Remplit l'UI avec les infos du livre.

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
        binding.detailRatingPages.text = "$rating • $pages"

        binding.detailDescription.text = book.description?.takeIf { it.isNotBlank() }
            ?: "Aucune description disponible."

        // Bouton ajouter à la bibliothèque
        binding.detailAddButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    libraryRepository.saveBook(book)
                    Log.d(tag, "Livre sauvegardé en BDD : ${book.title}")
                    Toast.makeText(
                        this@BookDetailActivity,
                        "📚 ${book.title} ajouté à votre bibliothèque",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Log.e(tag, "Erreur ajout biblio", e)
                    Toast.makeText(
                        this@BookDetailActivity,
                        "Erreur lors de l'ajout : ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}