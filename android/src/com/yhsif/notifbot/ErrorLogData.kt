package com.yhsif.notifbot

data class ErrorLogData(
  val timestamp: Long,
  val errorType: String,
  val message: String,
  val details: String
)
