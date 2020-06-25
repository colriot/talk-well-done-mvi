package xyz.ryabov.sample.mvi.search

import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import xyz.ryabov.sample.mvi.api.Api
import xyz.ryabov.sample.mvi.api.ProductionApi
import xyz.ryabov.sample.mvi.redux.Action
import xyz.ryabov.sample.mvi.redux.MviView
import xyz.ryabov.sample.mvi.redux.Store

class SearchBinder : ViewModel() {

  private val api: Api = ProductionApi()
  private val uiScheduler = AndroidSchedulers.mainThread()

  private val store: Store<Action, UiState> = Store(
      SearchReducer(),
      listOf(SearchMiddleware(api, uiScheduler), SuggestionsMiddleware(api, uiScheduler)),
      uiScheduler,
      UiState()
  )

  private val wiring = store.wire()
  private var viewBinding: Disposable? = null

  override fun onCleared() {
    wiring.dispose()
  }

  fun bind(view: MviView<Action, UiState>) {
    viewBinding = store.bind(view)
  }

  fun unbind() {
    viewBinding?.dispose()
  }
}
