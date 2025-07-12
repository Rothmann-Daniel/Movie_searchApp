package com.example.uithread.util


import android.content.Context
import com.example.uithread.data.network.MoviesRepositoryImpl
import com.example.uithread.data.network.RetrofitNetworkClient
import com.example.uithread.domain.api.MoviesInteractor
import com.example.uithread.domain.api.MoviesRepository
import com.example.uithread.domain.impl.MoviesInteractorImpl
import com.example.uithread.presentation.movies.MoviesSearchPresenter
import com.example.uithread.presentation.poster.PosterPresenter
import com.example.uithread.presentation.poster.PosterView

object Creator {
    private fun getMoviesRepository(context: Context): MoviesRepository {
        return MoviesRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideMoviesInteractor(context: Context): MoviesInteractor {
        return MoviesInteractorImpl(getMoviesRepository(context))
    }

    fun provideMoviesSearchPresenter(context: Context): MoviesSearchPresenter {
        return MoviesSearchPresenter(context)
    }

    fun providePosterPresenter(
        posterView: PosterView,
        imageUrl: String
    ): PosterPresenter {
        return PosterPresenter(posterView, imageUrl)
    }
}