package xyz.ryabov.sample.mvi.redux

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable

class Binder<A : Action, S : State>(private val store: Store<A, S>) : ViewModel() {

  private val wiring = store.wire()
  private var viewBinding: Disposable? = null

  override fun onCleared() {
    wiring.dispose()
  }

  fun bind(view: MviView<A, S>) {
    viewBinding = store.bind(view)
  }

  fun unbind() {
    viewBinding?.dispose()
  }
}
