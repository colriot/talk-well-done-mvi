package xyz.ryabov.sample.mvi.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xyz.ryabov.sample.mvi.R

class ItemAdapter(val onClick: (String) -> Unit) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
  private val items = arrayListOf<String>()

  fun replaceWith(newItems: List<String>) {
    items.clear()
    items.addAll(newItems)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false) as TextView
    return ViewHolder(view)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(items[position])
  }

  inner class ViewHolder(private val view: TextView) : RecyclerView.ViewHolder(view) {
    init {
      view.setOnClickListener { onClick(view.text.toString()) }
    }

    fun bind(item: String) {
      view.text = item
    }
  }
}
