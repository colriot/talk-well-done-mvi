package xyz.ryabov.sample.mvi.api

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import xyz.ryabov.sample.mvi.domain.Movie
import xyz.ryabov.sample.mvi.domain.movies
import xyz.ryabov.sample.mvi.flow.Observable

class ProductionApi : Api {

  override fun search(q: String): Observable<Movie> {
    return flow {
      delay(2_000)
      emit(movies.first { it.title.contains(q, ignoreCase = true) })
    }
  }

  override fun suggestions(query: String): Observable<List<String>> {
    return flow {
      val suggestions = if (query.isBlank()) {
        emptyList()
      } else {
        movies.filter { it.title.contains(query, ignoreCase = true) }.map { it.title }
      }
      emit(suggestions)
    }
  }
}
