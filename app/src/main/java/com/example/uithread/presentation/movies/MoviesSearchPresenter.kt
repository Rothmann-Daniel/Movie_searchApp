package com.example.uithread.presentation.movies

import android.app.Activity
import android.content.Context
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uithread.util.Creator
import com.example.uithread.domain.models.Movie
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uithread.R
import com.example.uithread.domain.api.MoviesInteractor
import com.example.uithread.ui.movies.MoviesAdapter

class MoviesSearchPresenter(
    private val view: MoviesView,
    private val context: Context,
    private val adapter: MoviesAdapter,
) {
    //private val SEARCH_REQUEST_TOKEN = Any()
    private val movies = mutableListOf<Movie>()
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
                                showEmptyState("Ничего не найдено")
                            } else {
                                adapter.updateMovies(movies) // Используем updateMovies
                                view.showMoviesList(true)
                            }
                        }

                        errorMessage?.let { showError(it) }
                    }
                }
            })
        } else {
            showEmptyState("Введите запрос для поиска")
        }
    }

    private fun showEmptyState(message: String) {
        adapter.updateMovies(emptyList()) // Очищаем адаптер
        view.changePlaceholderText(message)
        view.showPlaceholderMessage(true)
        view.showMoviesList(false)
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        showEmptyState("Ошибка при загрузке")
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