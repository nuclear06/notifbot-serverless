# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep all classes in our package
-keep class com.yhsif.notifbot.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Security Crypto
-keep class androidx.security.crypto.** { *; }

# Google Tink (used by Security Crypto)
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**

# Google Error Prone annotations (missing at runtime, safe to ignore)
-dontwarn com.google.errorprone.annotations.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep notification listener service
-keep public class * extends android.service.notification.NotificationListenerService

# Keep Kotlin Metadata
-keep class kotlin.Metadata { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
