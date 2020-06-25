package xyz.ryabov.sample.mvi.search

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import xyz.ryabov.sample.mvi.api.Api
import xyz.ryabov.sample.mvi.domain.Movie
import xyz.ryabov.sample.mvi.flow.Observable
import xyz.ryabov.sample.mvi.flow.withLatestFrom
import xyz.ryabov.sample.mvi.redux.Action
import xyz.ryabov.sample.mvi.redux.Middleware

class SearchMiddleware(
    private val api: Api,
    private val dispatcher: CoroutineDispatcher
) : Middleware<Action, UiState> {

  override fun bind(actions: Observable<Action>, state: Observable<UiState>): Observable<Action> {
    return actions.filterIsInstance<UiAction.SearchAction>()
        .withLatestFrom(state) { action, currentState -> action to currentState }
        .flatMapMerge { (action, _) ->
          api.search(action.query)
              .map<Movie, InternalAction> { result ->
                InternalAction.SearchSuccessAction(result)
              }
              .catch { e -> emit(InternalAction.SearchFailureAction(e)) }
              .flowOn(dispatcher)
              .onStart { emit(InternalAction.SearchLoadingAction) }
        }
  }
}
