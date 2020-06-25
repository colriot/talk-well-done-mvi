package xyz.ryabov.sample.mvi.flow

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun View.clicks(): Flow<Unit> {
  return callbackFlow {
    setOnClickListener { offer(Unit) }
    awaitClose {
      setOnClickListener(null)
    }
  }
}

fun TextView.textChanges(): Flow<CharSequence> {
  return callbackFlow {
    val listener = object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        offer(s)
      }
    }
    addTextChangedListener(listener)
    awaitClose {
      removeTextChangedListener(listener)
    }
  }
}
