package com.example.uithread.presentation.movies

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.uithread.R
import com.example.uithread.domain.api.MoviesInteractor
import com.example.uithread.domain.models.Movie
import com.example.uithread.ui.movies.model.MoviesState
import com.example.uithread.util.Creator

class MoviesSearchPresenter(
    private var view: MoviesView? = null,
    private val context: Context,
) {

    private val moviesInteractor = Creator.provideMoviesInteractor(context)
    private val handler = Handler(Looper.getMainLooper())
    private val movies = mutableListOf<Movie>()

    private var lastSearchText: String? = null

    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText)
    }

    fun attachView(view: MoviesView) {
        this.view = view
    }

    fun detachView() {
        this.view = null
    }

    fun searchDebounce(changedText: String) {
        this.lastSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }


    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            view?.render(
                MoviesState.Loading
            )

            moviesInteractor.searchMovies(newSearchText, object : MoviesInteractor.MoviesConsumer {
                override fun consume(foundMovies: List<Movie>?, errorMessage: String?) {
                    handler.post {
                        if (foundMovies != null) {
                            movies.clear()
                            movies.addAll(foundMovies)
                        }

                        when {
                            errorMessage != null -> {
                                view?.render(
                                    MoviesState.Error(
                                        errorMessage = context.getString(R.string.something_went_wrong),
                                    )
                                )
                                view?.showToast(errorMessage)
                            }

                            movies.isEmpty() -> {
                                view?.render(
                                    MoviesState.Empty(
                                        message = context.getString(R.string.nothing_found),
                                    )
                                )
                            }

                            else -> {
                                view?.render(
                                    MoviesState.Content(
                                        movies = movies,
                                    )
                                )
                            }
                        }

                    }
                }
            })
        }
    }


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    fun onCreate() {
    }

    fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
    }


}