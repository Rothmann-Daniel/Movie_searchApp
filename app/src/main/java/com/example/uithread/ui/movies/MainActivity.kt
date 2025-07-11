package com.example.uithread.ui.movies

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uithread.ui.poster.PosterActivity
import com.example.uithread.R
import com.example.uithread.domain.models.Movie
import com.example.uithread.presentation.movies.MoviesView
import com.example.uithread.util.Creator

class MainActivity : Activity(), MoviesView {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val movies = ArrayList<Movie>()

    private var textWatcher: TextWatcher? = null

    private val adapter = MoviesAdapter { movie ->
        if (clickDebounce()) {
            val intent = Intent(this, PosterActivity::class.java)
            intent.putExtra("poster", movie.image)
            startActivity(intent)
        }
    }

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())


    private val moviesSearchPresenter = Creator.provideMoviesSearchPresenter(
        moviesView = this,
        context = this,
        adapter = adapter,
    )

    private lateinit var queryInput: EditText
    private lateinit var placeholderMessage: LinearLayout
    private lateinit var moviesList: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Кусочек кода, который был в Presenter
        placeholderMessage = findViewById(R.id.placeholderMessage)
        queryInput = findViewById(R.id.queryInput)
        moviesList = findViewById(R.id.locations)
        progressBar = findViewById(R.id.progressBar)

        moviesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        moviesList.adapter = adapter

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                moviesSearchPresenter.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        textWatcher?.let { queryInput.addTextChangedListener(it) }

        moviesSearchPresenter.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { queryInput.removeTextChangedListener(it) }
        moviesSearchPresenter.onDestroy()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    // Добавляем методы для изменения видимости UI-элементов


    override fun showMoviesList(isVisible: Boolean) {
        moviesList.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showProgressBar(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    override fun showPlaceholderMessage(isVisible: Boolean) {
        placeholderMessage.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun changePlaceholderText(newPlaceholderText: String) {
        // Найдите TextView внутри LinearLayout
        val textView = placeholderMessage.findViewById<TextView>(R.id.text_message)
        textView.text = newPlaceholderText
    }

    override fun updateMoviesList(newMoviesList: List<Movie>) {
        adapter.updateMovies(newMoviesList) // Делегируем адаптеру
    }


    override fun showEmptyState(message: String) {
        adapter.updateMovies(emptyList())
        changePlaceholderText(message)
        showPlaceholderMessage(true)
        showMoviesList(false)
    }

    override fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        showEmptyState("Ошибка при загрузке")
    }

    //init dev_MVP
}
