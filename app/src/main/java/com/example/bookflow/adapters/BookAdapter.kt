package com.example.bookflow.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookflow.R
import com.example.bookflow.databinding.ItemBookBinding
import com.example.bookflow.models.Book

class BookAdapter(
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private var books: List<Book> = emptyList()

    class BookViewHolder(
        private val binding: ItemBookBinding,
        private val onBookClick: (Book) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.textViewTitle.text = book.title
            binding.textViewAuthor.text = book.getAuthorsAsString()

            binding.textViewPages.text = book.pageCount?.let { "$it pages" }
                ?: "Pages non précisées"

            if (book.averageRating != null) {
                binding.textViewRating.text = book.averageRating.toString()
                binding.textViewRating.visibility = View.VISIBLE
            } else {
                binding.textViewRating.text = "—"
                binding.textViewRating.visibility = View.VISIBLE
            }

            Glide.with(binding.imageViewCover.context)
                .load(book.coverUrl)
                .placeholder(R.drawable.ic_book_placeholder)
                .error(R.drawable.ic_book_placeholder)
                .centerCrop()
                .into(binding.imageViewCover)

            binding.root.setOnClickListener {
                onBookClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return BookViewHolder(binding, onBookClick)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int {
        return books.size
    }

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}