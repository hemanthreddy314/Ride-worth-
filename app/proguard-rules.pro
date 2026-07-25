# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve Room Database Entities and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao class *
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}

# Preserve Moshi and Serialized Data Classes
-keepclassmembers class * {
    @com.squareup.moshi.* <fields>;
}
-keep class com.example.data.models.** { *; }
-keep class com.example.garage.model.** { *; }
-keep class com.example.history.model.** { *; }
-keep class com.example.export.model.** { *; }
-keep class com.example.engine.model.** { *; }

# Preserve Line Numbers for Debugging
-keepattributes SourceFile,LineNumberTable

