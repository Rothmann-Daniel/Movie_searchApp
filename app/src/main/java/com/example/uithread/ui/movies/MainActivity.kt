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
import com.example.uithread.presentation.movies.MoviesSearchPresenter
import com.example.uithread.presentation.movies.MoviesView
import com.example.uithread.ui.movies.model.MoviesState
import com.example.uithread.util.Creator
import com.example.uithread.util.MoviesApplication

class MainActivity : Activity(), MoviesView {


    private var moviesSearchPresenter: MoviesSearchPresenter? = null

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


    private lateinit var queryInput: EditText
    private lateinit var placeholderMessage: LinearLayout
    private lateinit var moviesList: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        moviesSearchPresenter = (this.application as? MoviesApplication)?.moviesSearchPresenter

        if (moviesSearchPresenter == null) {
            moviesSearchPresenter = Creator.provideMoviesSearchPresenter(
                context = this.applicationContext, //устранение утечки Ссылка на уничтоженную Activity больше не хранится
            )
            (this.application as? MoviesApplication)?.moviesSearchPresenter = moviesSearchPresenter
        }

        moviesSearchPresenter?.attachView(this)


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
                moviesSearchPresenter?.searchDebounce( // Добавлен безопасный вызов ?.
                    changedText = s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        textWatcher?.let { queryInput.addTextChangedListener(it) }

        moviesSearchPresenter?.onCreate()
    }


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun showLoading() {
        setViewsVisibility(
            progressVisible = true,
            listVisible = false,
            placeholderVisible = false
        )
    }

    override fun showError(errorMessage: String) {
        setViewsVisibility(
            progressVisible = false,
            listVisible = false,
            placeholderVisible = true
        )
        placeholderMessage.findViewById<TextView>(R.id.text_message).text = errorMessage
    }

    override fun showEmpty(emptyMessage: String) {
        setViewsVisibility(
            progressVisible = false,
            listVisible = false,
            placeholderVisible = true
        )
        placeholderMessage.findViewById<TextView>(R.id.text_message).text = emptyMessage
    }

    override fun showContent(movies: List<Movie>) {
        setViewsVisibility(
            progressVisible = false,
            listVisible = true,
            placeholderVisible = false
        )
        adapter.updateMovies(movies)
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setViewsVisibility(
        progressVisible: Boolean,
        listVisible: Boolean,
        placeholderVisible: Boolean
    ) {
        progressBar.visibility = if (progressVisible) View.VISIBLE else View.GONE
        moviesList.visibility = if (listVisible) View.VISIBLE else View.GONE
        placeholderMessage.visibility = if (placeholderVisible) View.VISIBLE else View.GONE
    }

    override fun render(state: MoviesState) {
        when (state) {
            is MoviesState.Loading -> showLoading()
            is MoviesState.Content -> showContent(state.movies)
            is MoviesState.Error -> showError(state.errorMessage)
            is MoviesState.Empty -> showEmpty(state.message)
        }
    }


    override fun onStart() {
        super.onStart()
        moviesSearchPresenter?.attachView(this)
    }

    override fun onResume() {
        super.onResume()
        moviesSearchPresenter?.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        moviesSearchPresenter?.detachView()
    }

    override fun onStop() {
        super.onStop()
        moviesSearchPresenter?.detachView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        moviesSearchPresenter?.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()

        textWatcher?.let { queryInput.removeTextChangedListener(it) }
        moviesSearchPresenter?.detachView()
        moviesSearchPresenter?.onDestroy()

        if (isFinishing()) {
            // Очищаем ссылку на Presenter в Application
            (this.application as? MoviesApplication)?.moviesSearchPresenter = null
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }


}
