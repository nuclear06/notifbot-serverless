package com.yhsif.notifbot

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Telegram Bot API client for verification and chat ID retrieval.
 * Uses OkHttp (FOSS alternative to proprietary HTTP clients).
 */
object TelegramApi {
  
  private const val BASE_URL = "https://api.telegram.org"
  private const val TAG = "TelegramApi"
  
  private val client by lazy {
    OkHttpClient.Builder()
      .connectTimeout(10, TimeUnit.SECONDS)
      .readTimeout(10, TimeUnit.SECONDS)
      .build()
  }
  
  data class BotInfo(
    val id: Long,
    val name: String,
    val username: String
  )
  
  data class ChatInfo(
    val chatId: Long,
    val firstName: String,
    val lastName: String?,
    val username: String?,
    val fullName: String
  )
  
  /**
   * Verify bot token by calling /getMe API.
   * Returns bot information if token is valid.
   */
  suspend fun verifyBotToken(token: String): BotInfo? = withContext(Dispatchers.IO) {
    try {
      val url = "$BASE_URL/bot$token/getMe"
      val request = Request.Builder()
        .url(url)
        .get()
        .build()
      
      client.newCall(request).execute().use { response ->
        if (response.isSuccessful) {
          val responseBody = response.body?.string()
          if (responseBody != null) {
            val json = JSONObject(responseBody)
            
            if (json.getBoolean("ok")) {
              val result = json.getJSONObject("result")
              return@withContext BotInfo(
                id = result.getLong("id"),
                name = result.getString("first_name"),
                username = result.getString("username")
              )
            }
          }
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error verifying bot token", e)
    }
    null
  }
  
  /**
   * Get chat ID by calling /getUpdates API.
   * Returns chat information from the latest private message.
   */
  suspend fun getChatId(token: String): ChatInfo? = withContext(Dispatchers.IO) {
    try {
      val url = "$BASE_URL/bot$token/getUpdates"
      val request = Request.Builder()
        .url(url)
        .get()
        .build()
      
      client.newCall(request).execute().use { response ->
        if (response.isSuccessful) {
          val responseBody = response.body?.string()
          if (responseBody != null) {
            val json = JSONObject(responseBody)
            
            if (json.getBoolean("ok")) {
              val results = json.getJSONArray("result")
              if (results.length() > 0) {
                // Get the last message
                val lastUpdate = results.getJSONObject(results.length() - 1)
                
                if (!lastUpdate.has("message")) {
                  return@withContext null
                }
                
                val message = lastUpdate.getJSONObject("message")
                val from = message.getJSONObject("from")
                val chat = message.getJSONObject("chat")
                
                // Only process private messages
                if (chat.getString("type") == "private") {
                  val firstName = from.getString("first_name")
                  val lastName = if (from.has("last_name")) from.getString("last_name") else null
                  val username = if (from.has("username")) from.getString("username") else null
                  
                  return@withContext ChatInfo(
                    chatId = chat.getLong("id"),
                    firstName = firstName,
                    lastName = lastName,
                    username = username,
                    fullName = if (lastName != null) "$firstName $lastName" else firstName
                  )
                }
              }
            }
          }
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error getting chat ID", e)
    }
    null
  }
  
  /**
   * Send a message to Telegram.
   * Returns true if successful.
   */
  suspend fun sendMessage(
    token: String,
    chatId: Long,
    text: String
  ): Boolean = withContext(Dispatchers.IO) {
    try {
      val url = "$BASE_URL/bot$token/sendMessage"
      
      val requestBody = FormBody.Builder()
        .add("chat_id", chatId.toString())
        .add("text", text)
        .build()
      
      val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()
      
      client.newCall(request).execute().use { response ->
        return@withContext response.isSuccessful
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error sending message", e)
      return@withContext false
    }
  }
}
