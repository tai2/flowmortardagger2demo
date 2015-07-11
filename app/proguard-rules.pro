# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/tai2/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class net.tai2.flowmortardagger2demo.model.** { *; }
##---------------End: proguard configuration for Gson  ----------


##---------------Begin: proguard configuration for Butter Knife  ----------
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
##---------------End: proguard configuration for Butter Knife  ----------


##---------------Begin: proguard configuration for Realm  ----------
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.**
-dontwarn javax.**
-dontwarn io.realm.**
##---------------End: proguard configuration for Realm  ----------


##---------------Begin: proguard configuration for Dagger 2  ----------
-keep @dagger.Component public class *
-keep @dagger.Module public class * { *; }
-keep class net.tai2.flowmortardagger2demo.**Dagger** { *; }
##---------------End: proguard configuration for Dagger 2  ----------
