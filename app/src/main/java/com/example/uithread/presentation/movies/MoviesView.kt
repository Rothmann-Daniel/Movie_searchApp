package com.example.uithread.presentation.movies

import com.example.uithread.domain.models.Movie

interface MoviesView {

    fun showPlaceholderMessage(isVisible: Boolean)

    fun showMoviesList(isVisible: Boolean)

    fun showProgressBar(isVisible: Boolean)

    fun changePlaceholderText(newPlaceholderText: String)

    fun updateMoviesList(newMoviesList: List<Movie>)

    fun showEmptyState(message: String)

    fun showError(errorMessage: String)

}