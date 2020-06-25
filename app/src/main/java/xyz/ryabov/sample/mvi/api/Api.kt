package xyz.ryabov.sample.mvi.api

import io.reactivex.Observable
import xyz.ryabov.sample.mvi.domain.Movie

interface Api {
  fun search(q: String): Observable<Movie>
  fun suggestions(query: String): Observable<List<String>>
}