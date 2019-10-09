package xyz.ryabov.sample.mvi.search

import io.reactivex.Observable
import io.reactivex.Scheduler
import xyz.ryabov.sample.mvi.api.Api
import xyz.ryabov.sample.mvi.ofType
import xyz.ryabov.sample.mvi.redux.Action
import xyz.ryabov.sample.mvi.redux.Middleware
import xyz.ryabov.sample.mvi.withLatestFrom

class SuggestionsMiddleware(private val api: Api, private val uiScheduler: Scheduler) : Middleware<Action, UiState> {
  override fun bind(actions: Observable<Action>, state: Observable<UiState>): Observable<Action> {
    return actions.ofType<UiAction.LoadSuggestionsAction>()
        .withLatestFrom(state) { action, currentState -> action to currentState }
        .switchMap { (action, _) ->
          api.suggestions(action.query)
              .onErrorReturnItem(emptyList())
              .map { result -> InternalAction.SuggestionsLoadedAction(result) }
              .observeOn(uiScheduler)
        }
  }
}
