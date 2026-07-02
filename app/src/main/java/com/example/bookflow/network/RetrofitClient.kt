package com.example.bookflow.network

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object RetrofitClient {
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    private const val TIMEOUT_SECONDS = 30L

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    private val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).connectTimeout(TIMEOUT_SECONDS,TimeUnit.SECONDS).readTimeout(TIMEOUT_SECONDS,
        TimeUnit.SECONDS).writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS).build()


    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    val api: GoogleBooksService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GoogleBooksService::class.java)
    }





}