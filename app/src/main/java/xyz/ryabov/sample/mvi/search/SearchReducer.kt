package xyz.ryabov.sample.mvi.search

import xyz.ryabov.sample.mvi.redux.Action
import xyz.ryabov.sample.mvi.redux.Reducer

class SearchReducer : Reducer<UiState, Action> {
  override fun reduce(state: UiState, action: Action): UiState {
    return when (action) {
      InternalAction.SearchLoadingAction        -> state.copy(loading = true, error = null, suggestions = emptyList())
      is InternalAction.SearchSuccessAction     -> state.copy(
          loading = false,
          data = action.data,
          error = null,
          suggestions = emptyList()
      )
      is InternalAction.SearchFailureAction     -> state.copy(loading = false, error = action.error)
      is InternalAction.SuggestionsLoadedAction -> state.copy(suggestions = action.suggestions)
      is UiAction.LoadSuggestionsAction, is UiAction.SearchAction                               -> state
    }
  }
}
