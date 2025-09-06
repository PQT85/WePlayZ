# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-dontobfuscate
#-dontoptimize



-keep class vn.mgjsc.sdk.SDKManager$PaymentUserCallback, vn.mgjsc.sdk.SDKManager$AuthUserInterface {
<methods>;
}

-keepclassmembers class vn.mgjsc.sdk.SDKManager {

 public *** requestPermissionForPN(...);
 public *** login(...);
 public static *** login*(...);
 public *** logout(...);
 public static *** logout*(...);
 public *** initSDK(...);
 public static *** initSDK(...);
 public ***payment(...);
 public static ***payment*(...);

 public static ***showBanner(...);
 public ***showBanner(...);

}

-keep class vn.mgjsc.sdk.models.UserAccountModel, vn.mgjsc.sdk.models.PaymentDataGameModel{
<methods>;
<fields>;
}

-keep class vn.mgjsc.sdk.models.* {
<methods>;
<fields>;
}
-keep class vn.mgjsc.sdk.utils.ImmersiveControl {
<methods>;
}
-keepattributes *Annotation*

-keepattributes InnerClasses


# for adjust sdk
-keep class com.adjust.sdk.** { *; }

-keep class com.google.android.gms.common.ConnectionResult {

   int SUCCESS;

}

-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {

   com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);

}

-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {

   java.lang.String getId();

   boolean isLimitAdTrackingEnabled();

}

-keep public class com.android.installreferrer.** { *; }