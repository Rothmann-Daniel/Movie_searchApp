package com.example.uithread.util

import android.app.Application
import com.example.uithread.presentation.movies.MoviesSearchPresenter

class MoviesApplication : Application() {

    var moviesSearchPresenter: MoviesSearchPresenter? = null

}