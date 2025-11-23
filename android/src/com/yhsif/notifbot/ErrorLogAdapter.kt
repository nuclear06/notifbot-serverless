package com.yhsif.notifbot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ErrorLogAdapter(
  var list: MutableList<ErrorLogData>,
) : RecyclerView.Adapter<ErrorLogViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, vt: Int): ErrorLogViewHolder {
    val v = LayoutInflater
      .from(parent.getContext())
      .inflate(R.layout.error_log_item, parent, false)
    return ErrorLogViewHolder(v)
  }

  override fun onBindViewHolder(vh: ErrorLogViewHolder, i: Int) {
    val error = list.get(i)
    vh.setTime(error.timestamp)
    vh.setMessage(error.message)
    vh.setDetails(error.details)
  }

  override fun getItemCount(): Int = list.size

  override fun onAttachedToRecyclerView(rv: RecyclerView) {
    super.onAttachedToRecyclerView(rv)
  }

  fun clear() {
    list.clear()
    notifyDataSetChanged()
  }
}
