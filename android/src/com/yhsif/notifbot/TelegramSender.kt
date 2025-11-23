package com.yhsif.notifbot

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Sends notifications directly to Telegram Bot API.
 * Replacement for HttpSender with serverless architecture.
 * Uses OkHttp (FOSS alternative to Google's Cronet).
 */
class TelegramSender {
  
  companion object {
    private const val TAG = "TelegramSender"
    private const val BASE_URL = "https://api.telegram.org"
    
    private val client by lazy {
      OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()
    }
    
    /**
     * Send notification to Telegram.
     * 
     * @param ctx Android context
     * @param label Notification label/title
     * @param msg Notification message content
     * @param onSuccess Success callback
     * @param onFailure Failure callback
     * @param onNetFail Network failure callback
     */
    fun send(
      ctx: Context,
      label: String,
      msg: String,
      onSuccess: Runnable?,
      onFailure: Runnable?,
      onNetFail: Runnable?
    ) {
      CoroutineScope(Dispatchers.IO).launch {
        try {
          val storage = SecureStorage(ctx)
          val token = storage.getBotToken()
          val chatId = storage.getChatId()
          
          if (token == null || chatId == null) {
            Log.e(TAG, "Not configured: token or chat ID is missing")
            onFailure?.run()
            return@launch
          }
          
          val text = if (label.isNotEmpty()) {
            "$label\n$msg"
          } else {
            msg
          }
          
          val url = "$BASE_URL/bot$token/sendMessage"
          
          val requestBody = FormBody.Builder()
            .add("chat_id", chatId.toString())
            .add("text", text)
            .build()
          
          val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
          
          client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
              Log.e(TAG, "Network error sending message", e)
              ErrorLogActivity.logError(
                ctx,
                "Network Error",
                "Network failure while sending to Telegram",
                "Error: ${e.message}"
              )
              onNetFail?.run()
            }
            
            override fun onResponse(call: Call, response: Response) {
              response.use {
                if (response.isSuccessful) {
                  Log.i(TAG, "Message sent successfully")
                  onSuccess?.run()
                } else {
                  val body = response.body?.string() ?: ""
                  Log.e(TAG, "Failed to send message: HTTP ${response.code}, Body: $body")
                  ErrorLogActivity.logError(
                    ctx,
                    "Telegram API Error",
                    "HTTP ${response.code}: ${response.message}",
                    "Response: $body"
                  )
                  onFailure?.run()
                }
              }
            }
          })
          
        } catch (e: Exception) {
          Log.e(TAG, "Error preparing request", e)
          ErrorLogActivity.logError(
            ctx,
            "Request Error",
            "Failed to prepare Telegram request",
            "Error: ${e.message}\n${e.stackTraceToString()}"
          )
          onFailure?.run()
        }
      }
    }
  }
}
