package com.example.uithread.presentation.movies

import com.example.uithread.domain.models.Movie
import com.example.uithread.ui.movies.model.MoviesState
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType


interface MoviesView: MvpView {

    // Методы, меняющие внешний вид экрана
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun render(state: MoviesState)

    // Состояние загрузки
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showLoading()

    // Состояние ошибки
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showError(errorMessage: String)

    // Состояние пустого списка
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmpty(emptyMessage: String)

    // Состояние контента
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showContent(movies: List<Movie>)

    // Методы одноразовых событий
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showToast(additionalMessage: String)

}