package com.example.uithread.data.network

import com.example.uithread.data.dto.MoviesSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path



private const val IMDB_API_KEY = "k_zcuw1ytf"

interface IMDbApi {
    @GET("/en/API/SearchMovie/$IMDB_API_KEY/{expression}")
    fun findMovie(@Path("expression") expression: String): Call<MoviesSearchResponse>
}