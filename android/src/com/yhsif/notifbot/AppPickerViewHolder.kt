package com.yhsif.notifbot

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppPickerViewHolder(val v: View) : RecyclerView.ViewHolder(v) {

  fun setIcon(icon: Drawable) {
    v.findViewById<ImageView>(R.id.app_icon).setImageDrawable(icon)
  }

  fun setName(name: String) {
    v.findViewById<TextView>(R.id.app_name).setText(name)
  }
}
