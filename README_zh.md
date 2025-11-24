# NotifBot

一个将 Android 通知转发到 Telegram 的应用。

[English Documentation](README.md)

## 关于此 Fork

本项目是 [fishy/notifbot](https://github.com/fishy/notifbot) 的 fork，主要改动为删除了日志上传有关代码，移除原项目必须使用指定Telegram Bot的要求，本项目必须使用自己的Telegram Bot(无后端)，客户端将加密存储Bot Token，并直接调用Telegram Bot API发送通知，Telegram Bot API需要指定发送通知的目标chat id，所以理论上不会泄露信息。

这些改动出于以下几点考虑：
- 我不希望我的信息发送到无法审计代码机器人上
- 我不希望上传任何日志信息
- 在自用场景下，没有必要添加Bot后端

### 1. 隐私性修改

- 移除后端服务器依赖 , Android 客户端直接与 Telegram Bot API 通信 
- 移除原项目所有日志记录

### 2. 增加应用选择界面

保留原有方式，并新增应用选择器添加，支持搜索功能

### 3. 错误日志

内置错误日志查看器，用于排查通知发送失败问题

### 4. 锁屏检测

仅在屏幕锁定时发送通知，防止在使用设备时重复通知

## 构建

### 调试版本

```bash
./gradlew assembleDebug
```

### 发布版本

1. 复制 `keystore.properties.example` 为 `keystore.properties`
2. 编辑 `keystore.properties` 填入您的密钥库信息
3. 构建发布版 APK：

```bash
./gradlew assembleRelease
```

## 许可证

BSD 3-Clause License。详见 [LICENSE](LICENSE)。

