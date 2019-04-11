package xyz.ryabov.sample.mvi.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import xyz.ryabov.sample.mvi.R
import xyz.ryabov.sample.mvi.api.Api
import xyz.ryabov.sample.mvi.api.ProductionApi
import xyz.ryabov.sample.mvi.redux.Action
import xyz.ryabov.sample.mvi.redux.Binder
import xyz.ryabov.sample.mvi.redux.MviView
import xyz.ryabov.sample.mvi.redux.State
import xyz.ryabov.sample.mvi.redux.Store
import xyz.ryabov.sample.mvi.toast

class MainActivity : AppCompatActivity(), MviView<Action, UiState> {

  val factory = object : ViewModelProvider.Factory {
    val api: Api = ProductionApi()
    val uiScheduler = AndroidSchedulers.mainThread()
    val searchReducer = SearchReducer()
    val searchMiddleware = SearchMiddleware(api, uiScheduler)
    val suggestionsMiddleware = SuggestionsMiddleware(api, uiScheduler)
    val middlewares = listOf(searchMiddleware, suggestionsMiddleware)

    val store = Store(searchReducer, middlewares, uiScheduler, UiState())
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return Binder(store) as T
    }
  }

  private val recyclerAdapter = ItemAdapter()

  private val presenter by lazy {
    binder()
//    ViewModelProviders.of(this, factory)[Binder::class.java]
  }

  private val _actions by lazy {
    val clicks = submitBtn.clicks().map { UiAction.SearchAction(searchView.text.toString()) }
    val textChanges = searchView.textChanges().skipInitialValue().map { UiAction.LoadSuggestionsAction(it.toString()) }

    Observable.merge(clicks, textChanges)
  }

  override val actions
    get() = _actions as Observable<Action>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    recyclerView.adapter = recyclerAdapter
    recyclerView.layoutManager = LinearLayoutManager(this)

    presenter.bind(this)
  }

  override fun onDestroy() {
    presenter.unbind()
    super.onDestroy()
  }

  override fun render(state: UiState) {
    submitBtn.isEnabled = !state.loading
    progressView.visibility = if (state.loading) View.VISIBLE else View.GONE

//    recyclerAdapter.replaceWith(state.data)
    recyclerAdapter.replaceWith(state.suggestions)

    state.error?.let { toast(it.localizedMessage) }
  }

  private fun binder(): Binder<Action, UiState> {
    val api: Api = ProductionApi()
    val uiScheduler = AndroidSchedulers.mainThread()
    val searchReducer = SearchReducer()
    val searchMiddleware = SearchMiddleware(api, uiScheduler)
    val suggestionsMiddleware = SuggestionsMiddleware(api, uiScheduler)
    val middlewares = listOf(searchMiddleware, suggestionsMiddleware)
    val store = Store(searchReducer, middlewares, uiScheduler, UiState())

    return Binder(store)
  }
}

sealed class UiAction : Action {
  class SearchAction(val query: String) : UiAction()
  class LoadSuggestionsAction(val query: String) : UiAction()
}

sealed class InternalAction : Action {
  object SearchLoadingAction : InternalAction()
  class SearchSuccessAction(val data: String) : InternalAction()
  class SearchFailureAction(val error: Throwable) : InternalAction()
  class SuggestionsLoadedAction(val suggestions: List<String>) : InternalAction()
}


data class UiState(
    val loading: Boolean = false,
    val data: String? = null,
    val error: Throwable? = null,
    val suggestions: List<String>? = null
) : State

//typealias FeedState = LceState<String>
//
//data class LceState<out T>(
//    val loading: Boolean = false,
//    val data: T? = null,
//    val error: Throwable? = null
//)
