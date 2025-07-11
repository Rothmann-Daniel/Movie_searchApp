package com.example.uithread.ui.movies

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uithread.domain.models.Movie

class MoviesAdapter(
    private var movies: List<Movie>,
    private var onItemClick: (Movie) -> Unit  // <- Добавляем обработчик клика
) : RecyclerView.Adapter<MovieViewHolder>() {

    // Добавляем метод для обновления данных
    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(parent)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
        holder.itemView.setOnClickListener { onItemClick(movie) }  // Вешаем клик
    }

    override fun getItemCount(): Int = movies.size
}