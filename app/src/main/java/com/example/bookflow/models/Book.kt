package com.example.bookflow.models

data class Book (
    val id: String,
    val title: String,
    val authors: List<String> = emptyList(),
    val description: String? = null,
    val coverUrl: String? = null,
    val publishedDate: String? = null,
    val pageCount: Int? = null,
    val publisher: String? = null,
    val categories: List<String> = emptyList(),
    val averageRating: Double? = null
) {
    fun getAuthorsAsString(): String {
        return if (authors.isEmpty()) "Auteur inconnu" else authors.joinToString(", ")
    }

    fun hasCover(): Boolean = !coverUrl.isNullOrBlank()
}
