# NotifBot

An Android application that forwards notifications to Telegram.

[中文文档](README_zh.md)

## About This Fork

This is a fork of [fishy/notifbot](https://github.com/fishy/notifbot) with the following changes:

### 1. Serverless Architecture for Privacy

- Removed backend server dependency
- Android client communicates directly with Telegram Bot API
- No third-party servers involved
- Removed all logging from original project

### 2. App Selection Interface

- Added in-app application picker with search functionality
- Google Play sharing method still supported

### 3. Error Logging

- Built-in error log viewer for troubleshooting failed notifications
- Records HTTP errors and API responses

### 4. FOSS Compliance

- Replaced proprietary dependencies with FOSS alternatives (OkHttp)
- All dependencies are Apache License 2.0

## Build

### Debug Build

```bash
./gradlew assembleDebug
```

### Release Build

1. Copy `keystore.properties.example` to `keystore.properties`
2. Edit `keystore.properties` with your keystore information
3. Build release APK:

```bash
./gradlew assembleRelease
```

## License

BSD 3-Clause License. See [LICENSE](LICENSE) for details.
