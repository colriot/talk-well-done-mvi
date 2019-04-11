package xyz.ryabov.sample.mvi.redux

import io.reactivex.Observable

interface Action
interface State

interface Reducer<S : State, A : Action> {
  fun reduce(state: S, action: A): S
}

interface Middleware<A : Action, S : State> {
  fun bind(actions: Observable<A>, state: Observable<S>): Observable<A>
}

interface MviView<A : Action, S : State> {
  val actions: Observable<A>
  fun render(state: S)
}
