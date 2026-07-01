package com.example.bookflow.network

import com.example.bookflow.BuildConfig
import com.example.bookflow.dto.GoogleBooksDto
import com.example.bookflow.dto.VolumeDto
import retrofit2.http.Query
import retrofit2.http.GET
import retrofit2.http.Path

private const val API_KEY = "AIzaSyC9Mh6P8t6fuwkE2vF0oWOrGTNX_AkFhvw"
interface GoogleBooksService {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("startIndex") startIndex: Int = 0,
        @Query("key") apiKey: String = API_KEY
    ): GoogleBooksDto

    @GET("volumes/{volumeId}")
    suspend fun getBookById(
        @Path("volumeId") volumeId: String,
        @Query("key") apiKey: String = API_KEY
    ): VolumeDto

    @GET("volumes")
    suspend fun  getBooksByCategory(
        @Query("q") subject: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("key") apiKey: String = API_KEY
    ): GoogleBooksDto


}

