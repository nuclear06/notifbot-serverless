# NotifBot

一个将 Android 通知转发到 Telegram 的应用。

[English Documentation](README.md)

## 关于此 Fork

本项目是 [fishy/notifbot](https://github.com/fishy/notifbot) 的 fork，主要改动如下：

### 1. 无服务器架构，保证隐私

- 移除后端服务器依赖
- Android 客户端直接与 Telegram Bot API 通信
- 不经过任何第三方服务器
- 移除原项目所有日志记录

### 2. 应用选择界面

- 新增应用内选择器，支持搜索功能
- 仍保留 Google Play 分享方式

### 3. 错误日志

- 内置错误日志查看器，用于排查通知发送失败问题
- 记录 HTTP 错误和 API 响应

### 4. FOSS 合规

- 使用 FOSS 替代方案替换专有依赖（OkHttp）
- 所有依赖均为 Apache License 2.0

## 构建

```bash
./gradlew assembleRelease
```

## 许可证

BSD 3-Clause License。详见 [LICENSE](LICENSE)。

