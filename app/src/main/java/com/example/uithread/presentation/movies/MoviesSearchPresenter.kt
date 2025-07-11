package com.example.uithread.presentation.movies

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.uithread.domain.api.MoviesInteractor
import com.example.uithread.domain.models.Movie
import com.example.uithread.util.Creator

class MoviesSearchPresenter(
    private val view: MoviesView,
    private val context: Context,
) {
    //private val SEARCH_REQUEST_TOKEN = Any()
    private val moviesInteractor = Creator.provideMoviesInteractor(context)
    private val handler = Handler(Looper.getMainLooper())

    private var lastSearchText: String? = null

    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText)
    }

    fun searchDebounce(changedText: String) {
        this.lastSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            view.showProgressBar(true)
            view.showPlaceholderMessage(false)

            moviesInteractor.searchMovies(newSearchText, object : MoviesInteractor.MoviesConsumer {
                override fun consume(foundMovies: List<Movie>?, errorMessage: String?) {
                    handler.post {
                        view.showProgressBar(false)

                        foundMovies?.let { movies ->
                            if (movies.isEmpty()) {
                                view.showEmptyState("Ничего не найдено")
                            } else {
                                view.updateMoviesList(movies)
                                view.showMoviesList(true)
                            }
                        }

                        errorMessage?.let { view.showError(it) }
                    }
                }
            })
        } else {
            view.showEmptyState("Введите запрос для поиска")
        }
    }



    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    fun onCreate() {
        view.showPlaceholderMessage(true)
        view.changePlaceholderText("Введите запрос для поиска")
    }

    fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
    }


}