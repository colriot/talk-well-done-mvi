package xyz.ryabov.sample.mvi.api

import io.reactivex.Observable

interface Api {
  fun search(q: String): Observable<String>
  fun suggestions(query: String): Observable<List<String>>
}