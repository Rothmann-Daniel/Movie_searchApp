package com.example.uithread.util

import android.app.Activity
import android.content.Context
import com.example.uithread.data.network.MoviesRepositoryImpl
import com.example.uithread.data.network.RetrofitNetworkClient
import com.example.uithread.domain.api.MoviesInteractor
import com.example.uithread.domain.api.MoviesRepository
import com.example.uithread.domain.impl.MoviesInteractorImpl
import com.example.uithread.presentation.MoviesSearchController
import com.example.uithread.presentation.PosterController
import com.example.uithread.ui.movies.MoviesAdapter

object Creator {
    private fun getMoviesRepository(context: Context): MoviesRepository {
        return MoviesRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideMoviesInteractor(context: Context): MoviesInteractor {
        return MoviesInteractorImpl(getMoviesRepository(context))
    }

    fun provideMoviesSearchController(activity: Activity, adapter: MoviesAdapter): MoviesSearchController {
        return MoviesSearchController(activity, adapter)
    }

    fun providePosterController(activity: Activity): PosterController {
        return PosterController(activity)
    }
}