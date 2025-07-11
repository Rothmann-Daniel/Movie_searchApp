package com.example.uithread.presentation.movies

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.uithread.R
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
    private val movies = mutableListOf<Movie>()

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
            view.showLoading()

            moviesInteractor.searchMovies(newSearchText, object : MoviesInteractor.MoviesConsumer {
                override fun consume(foundMovies: List<Movie>?, errorMessage: String?) {
                    handler.post {
                        when {
                            errorMessage != null -> {
                                view.showError(context.getString(R.string.something_went_wrong))
                                view.showToast(errorMessage)
                            }
                            foundMovies.isNullOrEmpty() -> {
                                view.showEmpty(context.getString(R.string.nothing_found))
                            }
                            else -> {
                                view.showContent(foundMovies)
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