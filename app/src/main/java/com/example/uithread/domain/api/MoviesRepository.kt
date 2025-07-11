package com.example.uithread.domain.api

import com.example.uithread.domain.models.Movie
import com.example.uithread.util.Resource

interface MoviesRepository {
    fun searchMovies(expression: String): Resource<List<Movie>>
}