package xyz.ryabov.sample.mvi.redux

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import xyz.ryabov.sample.mvi.flow.withLatestFrom

class Store<A : Action, S : State>(
    private val reducer: Reducer<S, A>,
    private val middlewares: List<Middleware<A, S>>,
    initialState: S
) {
  private val stateRelay = MutableStateFlow(initialState)
  private val actionsRelay = BroadcastChannel<A>(1)
  private val actionsFlow get() = actionsRelay.asFlow()

  fun wire(scope: CoroutineScope) {
    actionsFlow
        .withLatestFrom(stateRelay) { action, state ->
          reducer.reduce(state, action)
        }
        .distinctUntilChanged()
        .onEach {
          stateRelay.value = it
        }
        .launchIn(scope)

    middlewares
        .map { it.bind(actionsFlow, stateRelay) }
        .asFlow()
        .flattenMerge(middlewares.size)
        .onEach {
          actionsRelay.offer(it)
        }
        .launchIn(scope)
  }

  fun bind(view: MviView<A, S>, uiScope: CoroutineScope) {
    stateRelay
        .onEach {
          view.render(it)
        }
        .launchIn(uiScope)

    view.actions
        .onEach {
          actionsRelay.offer(it)
        }
        .launchIn(uiScope)
  }
}
