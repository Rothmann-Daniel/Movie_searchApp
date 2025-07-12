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
    private val context: Context,
) {

    private var view: MoviesView? = null
    private var lastSearchText: String? = null
    private var latestSearchText: String? = null
    private val moviesInteractor = Creator.provideMoviesInteractor(context)
    private val handler = Handler(Looper.getMainLooper())
    private var state: MoviesState? = null


    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText)
    }

    fun attachView(view: MoviesView) {
        this.view = view
        state?.let { view.render(it) }
    }

    fun detachView() {
        this.view = null
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.lastSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }


    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(MoviesState.Loading)
            try {
            moviesInteractor.searchMovies(newSearchText, object : MoviesInteractor.MoviesConsumer {
                override fun consume(foundMovies: List<Movie>?, errorMessage: String?) {
                    handler.post {
                        val movies = mutableListOf<Movie>()
                        if (foundMovies != null) {
                            movies.addAll(foundMovies)
                        }

                        when {
                            errorMessage != null -> {
                                renderState(
                                    MoviesState.Error(

                                        errorMessage = context.getString(R.string.something_went_wrong),
                                    )
                                )
                                view?.showToast(errorMessage)
                            }

                            movies.isEmpty() -> {
                                renderState(
                                    MoviesState.Empty(
                                        message = context.getString(R.string.nothing_found),
                                    )
                                )
                            }

                            else -> {
                                renderState(
                                    MoviesState.Content(
                                        movies = movies,
                                    )
                                )
                            }
                        }

                    }
                }
            })
        } catch (e: Exception) {
            handler.post {
                renderState(
                    MoviesState.Error(
                        errorMessage = context.getString(R.string.network_error)
                    )
                )
            }
        }
    }

    }

    private fun renderState(state: MoviesState) {
        this.state = state
        this.view?.render(state)
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