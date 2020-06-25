package xyz.ryabov.sample.mvi.api

import xyz.ryabov.sample.mvi.domain.Movie
import xyz.ryabov.sample.mvi.flow.Observable

interface Api {
  fun search(q: String): Observable<Movie>
  fun suggestions(query: String): Observable<List<String>>
}