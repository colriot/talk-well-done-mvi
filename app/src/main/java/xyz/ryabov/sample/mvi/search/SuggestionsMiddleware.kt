package xyz.ryabov.sample.mvi.search

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import xyz.ryabov.sample.mvi.api.Api
import xyz.ryabov.sample.mvi.flow.Observable
import xyz.ryabov.sample.mvi.flow.withLatestFrom
import xyz.ryabov.sample.mvi.redux.Action
import xyz.ryabov.sample.mvi.redux.Middleware

class SuggestionsMiddleware(
    private val api: Api,
    private val dispatcher: CoroutineDispatcher
) : Middleware<Action, UiState> {

  override fun bind(actions: Observable<Action>, state: Observable<UiState>): Observable<Action> {
    return actions.filterIsInstance<UiAction.LoadSuggestionsAction>()
        .withLatestFrom(state) { action, currentState -> action to currentState }
        .flatMapLatest { (action, _) ->
          api.suggestions(action.query)
              .catch { emit(emptyList()) }
              .map { result -> InternalAction.SuggestionsLoadedAction(result) }
              .flowOn(dispatcher)
        }
  }
}
