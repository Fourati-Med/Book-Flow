package com.example.bookflow.repositories

import com.example.bookflow.dto.VolumeDto
import com.example.bookflow.models.Book
import com.example.bookflow.network.GoogleBooksService
import com.example.bookflow.network.RetrofitClient

class BookRepository (
    private val service: GoogleBooksService = RetrofitClient.api
) {
    suspend fun searchBooks(query: String, maxResults: Int = 20): List<Book> {
        val response = service.searchBooks(
            query = query,
            maxResults = maxResults
        )
        return response.items?.map { it.toBook() } ?: emptyList()
    }

    suspend fun getBookById(volumeId: String): Book {
        val volumeDto = service.getBookById(volumeId)
        return volumeDto.toBook()

    }

    suspend fun getBookByCategory(subject: String, maxResults: Int = 20): List<Book> {
        val response = service.getBooksByCategory(
            subject = "subject:$subject",
            maxResults = maxResults
        )
        return response.items?.map { it.toBook() } ?: emptyList()
    }

    private fun VolumeDto.toBook(): Book {
        val info = this.volumeInfo
        return Book(
            id = this.id,
            title = info?.title.orEmpty(),
            authors = info?.authors ?: emptyList(),
            description = info?.description,
            coverUrl = info?.imageLinks?.thumbnail?.replace("http://", "https://"),
            publishedDate = info?.publishedDate,
            pageCount = info?.pageCount,
            publisher = info?.publisher,
            categories = info?.categories ?: emptyList(),
            averageRating = info?.averageRating
        )
    }

}








