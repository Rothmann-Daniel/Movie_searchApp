package com.example.uithread.presentation.movies

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.uithread.R
import com.example.uithread.domain.api.MoviesInteractor
import com.example.uithread.domain.models.Movie
import com.example.uithread.ui.movies.model.MoviesState
import com.example.uithread.util.Creator
import moxy.MvpPresenter

class MoviesSearchPresenter(
    private val context: Context
) : MvpPresenter<MoviesView>() {

    private var lastSearchText: String? = null
    private var latestSearchText: String? = null
    private val moviesInteractor: MoviesInteractor = Creator.provideMoviesInteractor(context)
    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.lastSearchText = changedText
        this.latestSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            viewState.render(MoviesState.Loading)
            moviesInteractor.searchMovies(newSearchText, object : MoviesInteractor.MoviesConsumer {
                override fun consume(foundMovies: List<Movie>?, errorMessage: String?) {
                    handler.post {
                        when {
                            errorMessage != null -> {
                                viewState.render(
                                    MoviesState.Error(
                                        errorMessage = context.getString(R.string.something_went_wrong)
                                    )
                                )
                                viewState.showToast(errorMessage)
                            }

                            foundMovies.isNullOrEmpty() -> {
                                viewState.render(
                                    MoviesState.Empty(
                                        message = context.getString(R.string.nothing_found)
                                    )
                                )
                            }

                            else -> {
                                viewState.render(
                                    MoviesState.Content(
                                        movies = foundMovies ?: emptyList()
                                    )
                                )
                            }
                        }
                    }
                }
            })
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}