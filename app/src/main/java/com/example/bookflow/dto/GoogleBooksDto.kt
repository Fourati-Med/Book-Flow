package com.example.bookflow.dto

import com.squareup.moshi.JsonClass

//Représente la réponse complète de l'endpoint /volumes de Google Books.
@JsonClass(generateAdapter = true)
data class  GoogleBooksDto(
    val kind: String? = null,
    val totalItems: Int? = null,
    val items: List<VolumeDto>? = null
)
//Représente un livre individuel dans la liste des résultats.
@JsonClass(generateAdapter = true)
data class VolumeDto (
    val id: String,
    val volumeInfo: VolumeInfoDto? = null
)

//Contient toutes les informations détaillées d'un livre.

@JsonClass(generateAdapter = true)
data class VolumeInfoDto(
    val title: String? = null,
    val authors: List<String>? = null,
    val description: String? = null,
    val publishedDate: String? = null,
    val pageCount: Int? = null,
    val publisher: String? = null,
    val categories: List<String>? = null,
    val averageRating: Double? = null,
    val imageLinks: ImageLinksDto? = null

)

//Contient les URLs des couvertures du livre, à différentes résolutions.

@JsonClass(generateAdapter = true)
data class ImageLinksDto(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null
)