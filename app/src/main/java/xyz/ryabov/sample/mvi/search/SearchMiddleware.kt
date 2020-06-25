package xyz.ryabov.sample.mvi.search

import io.reactivex.Scheduler
import xyz.ryabov.sample.mvi.api.Api
import xyz.ryabov.sample.mvi.flow.Observable
import xyz.ryabov.sample.mvi.ofType
import xyz.ryabov.sample.mvi.redux.Action
import xyz.ryabov.sample.mvi.redux.Middleware
import xyz.ryabov.sample.mvi.withLatestFrom

class SearchMiddleware(private val api: Api, private val uiScheduler: Scheduler) : Middleware<Action, UiState> {
  override fun bind(actions: Observable<Action>, state: Observable<UiState>): Observable<Action> {
    return actions.ofType<UiAction.SearchAction>()
        .withLatestFrom(state) { action, currentState -> action to currentState }
        .flatMap { (action, _) ->
          api.search(action.query)
              .map<InternalAction> { result ->
                InternalAction.SearchSuccessAction(result)
              }
              .onErrorReturn { e -> InternalAction.SearchFailureAction(e) }
              .observeOn(uiScheduler)
              .startWith(InternalAction.SearchLoadingAction)
        }
  }
}
