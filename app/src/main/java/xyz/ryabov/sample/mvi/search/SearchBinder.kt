package xyz.ryabov.sample.mvi.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import xyz.ryabov.sample.mvi.api.Api
import xyz.ryabov.sample.mvi.api.ProductionApi
import xyz.ryabov.sample.mvi.redux.Action
import xyz.ryabov.sample.mvi.redux.MviView
import xyz.ryabov.sample.mvi.redux.Store

class SearchBinder : ViewModel() {

  private val api: Api = ProductionApi()
  private val ioDispatcher = Dispatchers.IO

  private val store: Store<Action, UiState> = Store(
      SearchReducer(),
      listOf(SearchMiddleware(api, ioDispatcher), SuggestionsMiddleware(api, ioDispatcher)),
      UiState()
  )

  init {
    store.wire(viewModelScope)
  }

  fun bind(view: MviView<Action, UiState>, uiScope: CoroutineScope) {
    store.bind(view, uiScope)
  }
}
