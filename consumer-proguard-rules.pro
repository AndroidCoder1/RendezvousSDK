# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/enoch/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# Workaround for conflict with certain OEM-modified versions of the Android appcompat
# support libs, especially Samsung + Android 4.2.2
# See this thread for more info:
#   https://code.google.com/p/android/issues/detail?can=2&start=0&num=100&q=&colspec=ID%20Type%20Status%20Owner%20Summary%20Stars&groupby=&sort=&id=78377
-keep class com.rancard.** { *; }
-keep class com.google.** { *; }
-dontwarn com.rancard.**
-keepattributes Exceptions, Signature, InnerClasses
#okhttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontnote okhttp3.**

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

#GSON
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

#Parcels
keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class org.parceler.Parceler$$Parcels