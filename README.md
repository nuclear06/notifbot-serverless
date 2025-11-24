# NotifBot

An Android application that forwards notifications to Telegram.

## About This Fork

This is a fork of [fishy/notifbot](https://github.com/fishy/notifbot). The main changes include removing log upload code, eliminating the requirement to use a specific Telegram Bot from the original project. This fork requires using your own Telegram Bot (serverless). The client encrypts and stores the Bot Token locally and directly calls the Telegram Bot API to send notifications. The Telegram Bot API requires specifying the target chat ID for sending notifications, so theoretically no information will be leaked.

These changes are based on the following considerations:
- I don't want my information sent to a bot whose code cannot be audited
- I don't want to upload any log information
- For personal use scenarios, there is no need to add a Bot backend

### 1. Privacy Modifications

- Removed backend server dependency, Android client communicates directly with Telegram Bot API
- Removed all logging from original project

### 2. App Selection Interface

Preserved original method and added app picker with search functionality

### 3. Error Logging

Built-in error log viewer for troubleshooting failed notifications

### 4. Screen Lock Detection

Send notifications only when screen is locked, preventing duplicate notifications when actively using the device

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
