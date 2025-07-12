package com.example.uithread.ui.movies


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
import com.example.uithread.R
import com.example.uithread.domain.models.Movie
import com.example.uithread.presentation.movies.MoviesSearchPresenter
import com.example.uithread.presentation.movies.MoviesView
import com.example.uithread.ui.movies.model.MoviesState
import com.example.uithread.ui.poster.PosterActivity
import com.example.uithread.util.Creator
import moxy.MvpActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class MainActivity : MvpActivity(), MoviesView {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    @InjectPresenter
    lateinit var presenter: MoviesSearchPresenter

    @ProvidePresenter
    fun providePresenter(): MoviesSearchPresenter {
        return Creator.provideMoviesSearchPresenter(applicationContext)
    }


    private val adapter = MoviesAdapter { movie ->
        if (clickDebounce()) {
            val intent = Intent(this, PosterActivity::class.java)
            intent.putExtra("poster", movie.image)
            startActivity(intent)
        }
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var textWatcher: TextWatcher? = null

    private lateinit var queryInput: EditText
    private lateinit var placeholderMessage: LinearLayout
    private lateinit var moviesList: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupTextWatcher()
    }

    private fun initViews() {
        placeholderMessage = findViewById(R.id.placeholderMessage)
        queryInput = findViewById(R.id.queryInput)
        moviesList = findViewById(R.id.locations)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        moviesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        moviesList.adapter = adapter
    }

    private fun setupTextWatcher() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.searchDebounce(s?.toString() ?: "") // Исправлено с moviesSearchPresenter на presenter
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher?.let { queryInput.addTextChangedListener(it) }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun showLoading() = setViewsVisibility(true, false, false)
    override fun showError(errorMessage: String) {
        setViewsVisibility(false, false, true)
        placeholderMessage.findViewById<TextView>(R.id.text_message).text = errorMessage
    }
    override fun showEmpty(emptyMessage: String) {
        setViewsVisibility(false, false, true)
        placeholderMessage.findViewById<TextView>(R.id.text_message).text = emptyMessage
    }
    override fun showContent(movies: List<Movie>) {
        setViewsVisibility(false, true, false)
        adapter.updateMovies(movies)
    }
    override fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun setViewsVisibility(
        progressVisible: Boolean,
        listVisible: Boolean,
        placeholderVisible: Boolean
    ) {
        progressBar.visibility = if (progressVisible) View.VISIBLE else View.GONE
        moviesList.visibility = if (listVisible) View.VISIBLE else View.GONE
        placeholderMessage.visibility = if (placeholderVisible) View.VISIBLE else View.GONE
    }

    override fun render(state: MoviesState) = when (state) {
        is MoviesState.Loading -> showLoading()
        is MoviesState.Content -> showContent(state.movies)
        is MoviesState.Error -> showError(state.errorMessage)
        is MoviesState.Empty -> showEmpty(state.message)
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { queryInput.removeTextChangedListener(it) }
    }
}
