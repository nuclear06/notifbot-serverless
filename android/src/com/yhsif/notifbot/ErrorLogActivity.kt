package com.yhsif.notifbot

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class ErrorLogActivity : AppCompatActivity() {

  companion object {
    private const val PREF_ERROR_LOG = "com.yhsif.notifbot.error_log"
    private const val KEY_ERRORS = "errors"
    private const val MAX_ERRORS = 100

    fun logError(ctx: Context, errorType: String, message: String, details: String = "") {
      val pref = ctx.getSharedPreferences(PREF_ERROR_LOG, 0)
      val errorsJson = pref.getString(KEY_ERRORS, "[]") ?: "[]"
      val errors = JSONArray(errorsJson)

      val error = JSONObject()
      error.put("timestamp", System.currentTimeMillis())
      error.put("type", errorType)
      error.put("message", message)
      error.put("details", details)

      errors.put(error)

      // Keep only the last MAX_ERRORS
      while (errors.length() > MAX_ERRORS) {
        errors.remove(0)
      }

      pref.edit {
        putString(KEY_ERRORS, errors.toString())
      }
    }

    fun getErrors(ctx: Context): List<ErrorLogData> {
      val pref = ctx.getSharedPreferences(PREF_ERROR_LOG, 0)
      val errorsJson = pref.getString(KEY_ERRORS, "[]") ?: "[]"
      val errors = JSONArray(errorsJson)
      val result = mutableListOf<ErrorLogData>()

      for (i in 0 until errors.length()) {
        val error = errors.getJSONObject(i)
        result.add(
          ErrorLogData(
            timestamp = error.getLong("timestamp"),
            errorType = error.getString("type"),
            message = error.getString("message"),
            details = error.optString("details", "")
          )
        )
      }

      return result.reversed() // Show newest first
    }

    fun clearErrors(ctx: Context) {
      val pref = ctx.getSharedPreferences(PREF_ERROR_LOG, 0)
      pref.edit {
        remove(KEY_ERRORS)
      }
    }
  }

  lateinit var adapter: ErrorLogAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_error_log)

    setSupportActionBar(findViewById<Toolbar>(R.id.app_bar))
    getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

    adapter = ErrorLogAdapter(getErrors(this).toMutableList())
    
    findViewById<RecyclerView>(R.id.error_list).let { rv ->
      rv.setAdapter(adapter)
      rv.setLayoutManager(LinearLayoutManager(this))
    }

    findViewById<Button>(R.id.btn_clear).setOnClickListener {
      clearErrors(this)
      adapter.clear()
      MainActivity.showToast(this, getString(R.string.errors_cleared))
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.getItemId() == android.R.id.home) {
      finish()
      return true
    }
    return super.onOptionsItemSelected(item)
  }
}
