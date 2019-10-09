package xyz.ryabov.sample.mvi

import android.content.Context
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
  this.add(disposable)
}

inline fun <reified R : Any> Observable<*>.ofType(): Observable<R> = ofType(R::class.java)

inline fun <T, U, R> Observable<T>.withLatestFrom(
    other: ObservableSource<U>,
    crossinline combiner: (T, U) -> R
): Observable<R> = withLatestFrom(other, BiFunction<T, U, R> { t, u -> combiner.invoke(t, u) })

fun Context.toast(message: String?) {
  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
