package xyz.ryabov.sample.mvi.api

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ProductionApi : Api {
  override fun search(q: String): Observable<String> {
    return Observable.fromCallable { "Abrakadabra" }
        .delay(2, TimeUnit.SECONDS)
  }

  override fun suggestions(query: String): Observable<List<String>> {
    return Observable.fromCallable {
      if (query.isBlank()) {
        emptyList()
      } else {
        listOf("Abrakadabra", "Abikoc", "ABatut").filter { it.startsWith(query) }
      }
    }
  }
}
