package xyz.ryabov.sample.mvi.redux

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import xyz.ryabov.sample.mvi.plusAssign
import xyz.ryabov.sample.mvi.withLatestFrom

class Store<A : Action, S : State>(
    private val reducer: Reducer<S, A>,
    private val middlewares: List<Middleware<A, S>>,
    private val uiScheduler: Scheduler,
    initialState: S
) {
  private val stateRelay = BehaviorRelay.createDefault<S>(initialState)
  private val actionsRelay = PublishRelay.create<A>()

  fun wire(): Disposable {
    val disposable = CompositeDisposable()

    disposable += actionsRelay
        .withLatestFrom(stateRelay) { action, state ->
          reducer.reduce(state, action)
        }
        .distinctUntilChanged()
        .subscribe(stateRelay::accept)


    disposable += Observable.merge(
        middlewares.map { it.bind(actionsRelay, stateRelay) }
    ).subscribe(actionsRelay::accept)

    return disposable
  }

  fun bind(view: MviView<A, S>): Disposable {
    val disposable = CompositeDisposable()
    disposable += wire()
    disposable += stateRelay.observeOn(uiScheduler).subscribe(view::render)
    disposable += view.actions.subscribe(actionsRelay::accept)
    return disposable
  }
}
