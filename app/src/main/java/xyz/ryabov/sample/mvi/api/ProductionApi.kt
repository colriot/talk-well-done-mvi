package xyz.ryabov.sample.mvi.api

import xyz.ryabov.sample.mvi.domain.Movie
import xyz.ryabov.sample.mvi.domain.movies
import xyz.ryabov.sample.mvi.flow.Observable
import java.util.concurrent.TimeUnit

class ProductionApi : Api {
  override fun search(q: String): Observable<Movie> {
    return Observable.fromCallable { movies.first { it.title.contains(q, ignoreCase = true) } }
        .delay(2, TimeUnit.SECONDS)
  }

  override fun suggestions(query: String): Observable<List<String>> {
    return Observable.fromCallable {
      if (query.isBlank()) {
        emptyList()
      } else {
        movies.filter { it.title.contains(query, ignoreCase = true) }.map { it.title }
      }
    }
  }
}
