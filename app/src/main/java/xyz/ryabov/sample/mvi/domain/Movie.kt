package xyz.ryabov.sample.mvi.domain

data class Movie(val title: String, val content: String) {
  override fun toString(): String {
    return "Movie(title=$title, content=${content.substring(0..20)}...)"
  }
}
