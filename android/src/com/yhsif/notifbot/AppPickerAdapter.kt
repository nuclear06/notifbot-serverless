package com.yhsif.notifbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AppPickerAdapter(
  var list: MutableList<PkgData>,
  val listener: View.OnClickListener,
) : RecyclerView.Adapter<AppPickerViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, vt: Int): AppPickerViewHolder {
    val v = LayoutInflater
      .from(parent.getContext())
      .inflate(R.layout.app_picker_item, parent, false)
    v.setOnClickListener(listener)
    return AppPickerViewHolder(v)
  }

  override fun onBindViewHolder(vh: AppPickerViewHolder, i: Int) {
    vh.setIcon(list.get(i).icon)
    vh.setName(list.get(i).name)
  }

  override fun getItemCount(): Int = list.size

  override fun onAttachedToRecyclerView(rv: RecyclerView) {
    super.onAttachedToRecyclerView(rv)
  }

  fun filter(query: String) {
    notifyDataSetChanged()
  }
}
