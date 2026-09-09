# ==============================================================================
# Solitaire Hyper Card Games - Production R8 / ProGuard Optimization Rules
# ==============================================================================

# Preserve line numbers and source file names for Google Play crash reporting
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# ------------------------------------------------------------------------------
# Kotlin Coroutines & Flow
# ------------------------------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ------------------------------------------------------------------------------
# Room Database
# ------------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# ------------------------------------------------------------------------------
# Moshi & JSON Serialization
# ------------------------------------------------------------------------------
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-keep class * extends com.squareup.moshi.JsonAdapter { *; }
-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}
-keepclasseswithmembers class * {
    @com.squareup.moshi.JsonClass *;
}
-keep @com.squareup.moshi.JsonQualifier interface * { *; }

# ------------------------------------------------------------------------------
# Retrofit & OkHttp
# ------------------------------------------------------------------------------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# ------------------------------------------------------------------------------
# Google Play Services & AdMob
# ------------------------------------------------------------------------------
-keep public class com.google.android.gms.ads.** {
    public *;
}
-keep public class com.google.ads.** {
    public *;
}
-keep class com.google.android.gms.ads.mediation.** { *; }
-keep class com.google.ads.mediation.** { *; }
-dontwarn com.google.android.gms.ads.**

# ------------------------------------------------------------------------------
# Firebase AI, Analytics & App Check
# ------------------------------------------------------------------------------
-dontwarn com.google.firebase.**
-keep class com.google.firebase.** { *; }

# ------------------------------------------------------------------------------
# App Models & Game Domain
# ------------------------------------------------------------------------------
-keep class com.solitaire.hyper.card.games.data.model.** { *; }
-keep class com.solitaire.hyper.card.games.data.preferences.** { *; }
-keep class com.solitaire.hyper.card.games.data.db.** { *; }
-keep class com.solitaire.hyper.card.games.game.** { *; }
-keep class com.solitaire.hyper.card.games.ads.** { *; }
