package com.example.uithread.presentation.movies

import com.example.uithread.domain.models.Movie
import com.example.uithread.ui.movies.model.MoviesState

interface MoviesView {

    // Методы, меняющие внешний вид экрана

    // Состояние загрузки
    fun showLoading()

    // Состояние ошибки
    fun showError(errorMessage: String)

    // Состояние пустого списка
    fun showEmpty(emptyMessage: String)

    // Состояние контента
    fun showContent(movies: List<Movie>)

    // Методы одноразовых событий

    fun showToast(additionalMessage: String)

    fun render(state: MoviesState)


}