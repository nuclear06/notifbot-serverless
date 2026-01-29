package com.yhsif.notifbot

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.yhsif.notifbot.settings.SettingsActivity

/**
 * Telegram configuration wizard activity.
 * Guides user through 3-step setup process:
 * 1. Verify Bot Token
 * 2. Send test message
 * 3. Confirm Chat ID
 */
class TelegramConfigActivity : AppCompatActivity() {
  
  private lateinit var storage: SecureStorage
  
  private lateinit var stepIndicator: TextView
  private lateinit var instructionText: TextView
  private lateinit var tokenInput: EditText
  private lateinit var nextButton: Button
  private lateinit var progressBar: ProgressBar
  
  private var currentStep = 1
  private var verifiedToken: String? = null
  private var botUsername: String? = null
  private var botName: String? = null
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_telegram_config)
    
    storage = SecureStorage(this)
    
    stepIndicator = findViewById(R.id.step_indicator)
    instructionText = findViewById(R.id.instruction_text)
    tokenInput = findViewById(R.id.token_input)
    nextButton = findViewById(R.id.next_button)
    progressBar = findViewById(R.id.progress_bar)
    
    nextButton.setOnClickListener { handleNextButton() }
    
    showStep1()
  }
  
  private fun showStep1() {
    currentStep = 1
    stepIndicator.text = "Step 1 of 3"
    instructionText.text = getString(R.string.telegram_step1_instruction)
    tokenInput.visibility = View.VISIBLE
    tokenInput.hint = "Bot Token"
    nextButton.text = "Verify Token"
  }
  
  private fun showStep2() {
    currentStep = 2
    stepIndicator.text = "Step 2 of 3"
    instructionText.text = getString(R.string.telegram_step2_instruction, botUsername)
    tokenInput.visibility = View.GONE
    nextButton.text = "I've Sent a Message"
  }
  
  private fun showStep3() {
    currentStep = 3
    stepIndicator.text = "Step 3 of 3"
    instructionText.text = "Retrieving your chat information..."
    nextButton.visibility = View.GONE
    progressBar.visibility = View.VISIBLE
  }
  
  private fun handleNextButton() {
    when (currentStep) {
      1 -> verifyToken()
      2 -> retrieveChatId()
    }
  }
  
  private fun verifyToken() {
    val token = tokenInput.text.toString().trim()
    
    if (token.isEmpty()) {
      showError("Please enter your Bot Token")
      return
    }
    
    progressBar.visibility = View.VISIBLE
    nextButton.isEnabled = false
    
    CoroutineScope(Dispatchers.Main).launch {
      val baseUrl = SettingsActivity.getTelegramEndpoint(this@TelegramConfigActivity)
      val botInfo = TelegramApi.verifyBotToken(token, baseUrl)
      
      progressBar.visibility = View.GONE
      nextButton.isEnabled = true
      
      if (botInfo != null) {
        verifiedToken = token
        botUsername = botInfo.username
        botName = botInfo.name
        
        // Show confirmation dialog
        AlertDialog.Builder(this@TelegramConfigActivity)
          .setTitle("Confirm Bot")
          .setMessage("Bot Name: ${botInfo.name}\nUsername: @${botInfo.username}\n\nIs this correct?")
          .setPositiveButton("Yes") { _, _ ->
            storage.saveBotToken(token)
            storage.saveBotInfo(botInfo.username, botInfo.name)
            showStep2()
          }
          .setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            tokenInput.text.clear()
          }
          .setCancelable(false)
          .show()
      } else {
        showError("Invalid Bot Token. Please check and try again.")
      }
    }
  }
  
  private fun retrieveChatId() {
    showStep3()
    
    CoroutineScope(Dispatchers.Main).launch {
      val baseUrl = SettingsActivity.getTelegramEndpoint(this@TelegramConfigActivity)
      val chatInfo = TelegramApi.getChatId(verifiedToken!!, baseUrl)
      
      progressBar.visibility = View.GONE
      
      if (chatInfo != null) {
        val usernameText = if (chatInfo.username != null) {
          "@${chatInfo.username}"
        } else {
          "(no username)"
        }
        
        // Show confirmation dialog
        AlertDialog.Builder(this@TelegramConfigActivity)
          .setTitle("Confirm User")
          .setMessage(
            "Chat ID: ${chatInfo.chatId}\n" +
            "Name: ${chatInfo.fullName}\n" +
            "Username: $usernameText\n\n" +
            "Is this correct?"
          )
          .setPositiveButton("Yes") { _, _ ->
            storage.saveChatId(chatInfo.chatId)
            storage.saveUserInfo(chatInfo.fullName, chatInfo.username)
            
            // Show success and finish
            AlertDialog.Builder(this@TelegramConfigActivity)
              .setTitle("Configuration Complete")
              .setMessage("Telegram notifications are now enabled!")
              .setPositiveButton("OK") { _, _ ->
                setResult(RESULT_OK)
                finish()
              }
              .setCancelable(false)
              .show()
          }
          .setNegativeButton("No") { _, _ ->
            showRetryDialog()
          }
          .setCancelable(false)
          .show()
      } else {
        showRetryDialog()
      }
    }
  }
  
  private fun showRetryDialog() {
    AlertDialog.Builder(this)
      .setTitle("No Message Found")
      .setMessage("Please send a message to @$botUsername on Telegram, then try again.")
      .setPositiveButton("Retry") { _, _ ->
        showStep2()
      }
      .setNegativeButton("Start Over") { _, _ ->
        verifiedToken = null
        botUsername = null
        botName = null
        storage.clear()
        showStep1()
      }
      .setCancelable(false)
      .show()
  }
  
  private fun showError(message: String) {
    AlertDialog.Builder(this)
      .setTitle("Error")
      .setMessage(message)
      .setPositiveButton("OK", null)
      .show()
  }
}
