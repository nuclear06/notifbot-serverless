package com.yhsif.notifbot

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ErrorLogViewHolder(val v: View) : RecyclerView.ViewHolder(v) {

  private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

  fun setTime(timestamp: Long) {
    val date = Date(timestamp)
    v.findViewById<TextView>(R.id.error_time).setText(dateFormat.format(date))
  }

  fun setMessage(message: String) {
    v.findViewById<TextView>(R.id.error_message).setText(message)
  }

  fun setDetails(details: String) {
    v.findViewById<TextView>(R.id.error_details).setText(details)
  }
}
