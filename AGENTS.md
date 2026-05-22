# 项目上下文：NotifBot (无服务器分支版)

## 项目概述
NotifBot 是一款 Android 应用程序，旨在将设备通知转发到指定的 Telegram 聊天。此版本是原始项目 `fishy/notifbot` 的一个注重隐私的**无服务器 (Serverless)** 分支。它直接由 Android 客户端与 Telegram Bot API 通信，无需中间服务器。

**核心特性：**
*   **直接集成 Telegram：** 本地加密存储 Bot Token；直接调用 Telegram API 发送通知。
*   **隐私保护：** 无中间服务器，不上传外部日志。
*   **应用过滤：** 用户可以自主选择需要转发通知的应用。
*   **智能转发：** 支持“仅在锁屏时转发”选项，避免在使用手机时收到重复提醒。
*   **可靠性：** 具备网络失败后的重试机制，并提供本地错误日志记录。

## 架构与核心组件
项目采用 Kotlin 开发，遵循标准的 Android 应用结构。

*   **`NotificationListener` (`NotificationListenerService`):** 核心服务，监听系统通知。它根据用户偏好（选定应用、屏幕状态、去重逻辑）过滤通知并触发发送流程。
*   **`TelegramSender`:** 负责构建并执行向 Telegram Bot API 发送数据的 HTTP 请求。
*   **`TelegramConfigActivity`:** 用于配置 Bot Token 和 Chat ID 的界面。
*   **`AppPickerActivity`:** 用于选择需要转发通知的应用程序的界面。
*   **`SecureStorage`:** 利用 Android 的 `EncryptedSharedPreferences` 安全地存储 Bot Token 等敏感凭据。

## 构建与运行指南

### 前置条件
*   JDK 17
*   Android SDK

### 构建命令
请使用项目根目录下的 Gradle Wrapper (`./gradlew`) 执行所有构建操作。

*   **编译 Debug 版本：**
    ```bash
    ./gradlew assembleDebug
    ```
    生成的 APK 位于 `android/build/outputs/apk/debug/`。

*   **编译 Release 版本：**
    1.  在根目录创建 `keystore.properties`（参考 `keystore.properties.example` 模板）。
    2.  填写您的签名信息（`storeFile`, `storePassword`, `keyAlias`, `keyPassword`）。
    3.  执行：
        ```bash
        ./gradlew assembleRelease
        ```

## 开发规范

*   **编程语言：** Kotlin (`.kt`)。
*   **异步/并发：** 使用 Kotlin 协程 (`GlobalScope.launch`, `Dispatchers`) 处理背景任务，如网络请求。
*   **网络请求：** 使用 `OkHttp` 库。
*   **存储：** 使用 `SharedPreferences` (以及 `EncryptedSharedPreferences`) 进行配置和状态持久化。
*   **隐私优先：** 禁止添加任何会外泄数据的日志机制。所有错误日志应存储在本地（参考 `ErrorLogActivity`）。
*   **代码检查：** 配置了 Android Lint，但在 Release 构建中设为不因错误终止 (`abortOnError = false`)。

## 关键文件
*   `android/AndroidManifest.xml`: 应用组件声明及权限配置 (`BIND_NOTIFICATION_LISTENER_SERVICE`)。
*   `android/src/com/yhsif/notifbot/NotificationListener.kt`: 捕获和处理通知的核心逻辑。
*   `android/src/com/yhsif/notifbot/TelegramSender.kt`: 发送数据到 Telegram 的逻辑。
*   `build.gradle.kts` (根目录及 Android 目录): 构建配置和依赖管理。