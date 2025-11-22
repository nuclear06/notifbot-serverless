package com.yhsif.notifbot

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Secure storage for sensitive data using Android Keystore System.
 * Stores Bot Token and Chat ID with encryption.
 */
class SecureStorage(context: Context) {
  
  private val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()
  
  private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
    context,
    PREFS_NAME,
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
  )
  
  companion object {
    private const val PREFS_NAME = "notifbot_secure_prefs"
    private const val KEY_BOT_TOKEN = "bot_token"
    private const val KEY_CHAT_ID = "chat_id"
    private const val KEY_BOT_USERNAME = "bot_username"
    private const val KEY_BOT_NAME = "bot_name"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_USERNAME = "user_username"
  }
  
  fun saveBotToken(token: String) {
    sharedPreferences.edit()
      .putString(KEY_BOT_TOKEN, token)
      .apply()
  }
  
  fun getBotToken(): String? {
    return sharedPreferences.getString(KEY_BOT_TOKEN, null)
  }
  
  fun saveChatId(chatId: Long) {
    sharedPreferences.edit()
      .putLong(KEY_CHAT_ID, chatId)
      .apply()
  }
  
  fun getChatId(): Long? {
    val value = sharedPreferences.getLong(KEY_CHAT_ID, -1)
    return if (value == -1L) null else value
  }
  
  fun saveBotInfo(username: String, name: String) {
    sharedPreferences.edit()
      .putString(KEY_BOT_USERNAME, username)
      .putString(KEY_BOT_NAME, name)
      .apply()
  }
  
  fun getBotUsername(): String? {
    return sharedPreferences.getString(KEY_BOT_USERNAME, null)
  }
  
  fun getBotName(): String? {
    return sharedPreferences.getString(KEY_BOT_NAME, null)
  }
  
  fun saveUserInfo(name: String, username: String?) {
    sharedPreferences.edit()
      .putString(KEY_USER_NAME, name)
      .putString(KEY_USER_USERNAME, username)
      .apply()
  }
  
  fun getUserName(): String? {
    return sharedPreferences.getString(KEY_USER_NAME, null)
  }
  
  fun getUserUsername(): String? {
    return sharedPreferences.getString(KEY_USER_USERNAME, null)
  }
  
  fun isConfigured(): Boolean {
    return getBotToken() != null && getChatId() != null
  }
  
  fun clear() {
    sharedPreferences.edit().clear().apply()
  }
}
