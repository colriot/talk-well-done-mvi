package xyz.ryabov.sample.mvi.search

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*
import xyz.ryabov.sample.mvi.R
import xyz.ryabov.sample.mvi.domain.Movie
import xyz.ryabov.sample.mvi.flow.Observable
import xyz.ryabov.sample.mvi.lazyUi
import xyz.ryabov.sample.mvi.redux.Action
import xyz.ryabov.sample.mvi.redux.MviView
import xyz.ryabov.sample.mvi.redux.State
import xyz.ryabov.sample.mvi.toast

class MainActivity : AppCompatActivity(), MviView<Action, UiState> {

  private val suggestionPicks = BehaviorSubject.create<String>()

  private val recyclerAdapter = ItemAdapter {
    suggestionPicks.onNext(it)
  }

  private val presenter: SearchBinder by viewModels()

  private val _actions by lazyUi {
    val clicks = submitBtn.clicks().map { UiAction.SearchAction(searchView.text.toString()) }
    val suggestions = suggestionPicks.map { UiAction.SearchAction(it) }
    val textChanges = searchView.textChanges().skipInitialValue().map { UiAction.LoadSuggestionsAction(it.toString()) }

    Observable.merge(clicks, suggestions, textChanges)
  }

  @Suppress("UNCHECKED_CAST")
  override val actions: Observable<Action>
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

    state.data?.let {
      titleView.text = it.title
      contentView.text = it.content
    } ?: run {
      titleView.text = null
      contentView.text = null
    }

    recyclerAdapter.replaceWith(state.suggestions ?: emptyList())

    state.error?.let { toast(it.localizedMessage) }
  }
}

sealed class UiAction : Action {
  class SearchAction(val query: String) : UiAction()
  class LoadSuggestionsAction(val query: String) : UiAction()
}

sealed class InternalAction : Action {
  object SearchLoadingAction : InternalAction()
  class SearchSuccessAction(val data: Movie) : InternalAction()
  class SearchFailureAction(val error: Throwable) : InternalAction()
  class SuggestionsLoadedAction(val suggestions: List<String>) : InternalAction()
}

data class UiState(
  val loading: Boolean = false,
  val data: Movie? = null,
  val error: Throwable? = null,
  val suggestions: List<String>? = null
) : State
