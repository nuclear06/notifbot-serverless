# Build Guide

## Quick Build

```bash
./gradlew assembleDebug
```

APK location: `android/build/outputs/apk/debug/android-debug.apk`

## Requirements

- JDK 17+
- Android SDK (API 35)

## Commands

```bash
# Debug build
./gradlew assembleDebug

# Install to device
./gradlew installDebug

# Release build (unsigned)
./gradlew assembleRelease

# Clean
./gradlew clean
```

## GitHub Actions

Automatic builds run on every push to `master` branch.

Download artifacts from: https://github.com/fishy/notifbot/actions

## Android Studio

1. Open project folder
2. Wait for Gradle sync
3. Build → Make Project
4. Run → Run 'app'

## Troubleshooting

### Java version error
```bash
java -version  # Should be 17+
export JAVA_HOME=/path/to/jdk17
```

### Build fails
```bash
./gradlew clean build --refresh-dependencies
```

### ADB not found
```bash
export PATH="$PATH:~/Android/Sdk/platform-tools"
```

## Dependencies

All dependencies are FOSS (Apache 2.0 licensed):
- AndroidX
- OkHttp  
- Kotlin Coroutines
- Security Crypto

See `android/build.gradle.kts` for versions.
