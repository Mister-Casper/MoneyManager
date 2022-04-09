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
-keepclassmembers,allowobfuscation class * {
@com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers class com.sgcdeveloper.moneymanager.data.db.entry.WalletEntry { <fields>; }
-keepclassmembers class com.sgcdeveloper.moneymanager.domain.model.Currency { <fields>; }
-keepclassmembers class com.sgcdeveloper.moneymanager.domain.model.Recurring { <fields>; }
-keepclassmembers class com.sgcdeveloper.moneymanager.data.db.entry.RecurringTransactionEntry { <fields>; }
-keepclassmembers class com.sgcdeveloper.moneymanager.data.db.entry.TransactionEntry { <fields>; }
-keepclassmembers class com.sgcdeveloper.moneymanager.domain.model.RecurringEndType { <fields>; }
-keepclassmembers class com.sgcdeveloper.moneymanager.domain.model.RecurringInterval { <fields>; }
-keepclassmembers class com.sgcdeveloper.moneymanager.domain.model.RecurringTransaction { <fields>; }
-keep class com.sgcdeveloper.moneymanager.domain.model.Currency
-keep class com.sgcdeveloper.moneymanager.domain.model.Recurring
-keep class com.sgcdeveloper.moneymanager.domain.model.RecurringEndType
-keep class com.sgcdeveloper.moneymanager.data.util.RecurringIntervalSaver
-keep class com.sgcdeveloper.moneymanager.data.db.entry.TransactionCategoryEntry
-keepclassmembers class com.sgcdeveloper.moneymanager.data.db.entry.TransactionCategoryEntry { <fields>; }
-keepclassmembers class com.sgcdeveloper.moneymanager.data.util.RecurringIntervalSaver { <fields>; }
-keep class com.sgcdeveloper.moneymanager.domain.model.RecurringInterval
-keep class com.sgcdeveloper.moneymanager.domain.model.RecurringTransaction
-keep class com.sgcdeveloper.moneymanager.domain.model.TransactionCategory
-keepclassmembers class com.sgcdeveloper.moneymanager.domain.model.TransactionCategory { <fields>; }
-keep class com.sgcdeveloper.moneymanager.util.Date
-keepclassmembers class com.sgcdeveloper.moneymanager.util.Date { <fields>; }

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class kotlin.Metadata { *; }
-verbose
-keepattributes InnerClasses
-keep class **.R
-keep class **.R$* {
    <fields>;
}
