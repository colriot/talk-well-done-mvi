package xyz.ryabov.sample.mvi.search

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
  private val items = arrayListOf<String>()

  fun replaceWith(newItems: List<String>?) {
//    fun replaceWith(newItems: String?) {
    items.clear()
    newItems?.let { items.addAll(it) }
//      newItems?.let { items.add(it) }
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(TextView(parent.context))
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(items[position])
  }

  class ViewHolder(private val view: TextView) : RecyclerView.ViewHolder(view) {
    fun bind(item: String) {
      view.text = item
    }
  }
}
