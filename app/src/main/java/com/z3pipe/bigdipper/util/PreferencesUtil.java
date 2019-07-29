package com.z3pipe.bigdipper.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;

public class PreferencesUtil {
    public static String PREFERENCE_NAME = "bigdipper";

    public PreferencesUtil() {
    }

    public static void setPreferenceName(String var0) {
        if (StringUtil.isEmpty(var0)) {
            PREFERENCE_NAME = "bigdipper";
        } else {
            PREFERENCE_NAME = var0;
        }
    }

    public static boolean putString(Context var0, String var1, String var2) {
        Editor var3;
        (var3 = var0.getSharedPreferences(PREFERENCE_NAME, 0).edit()).putString(var1, var2);
        return var3.commit();
    }

    public static String getString(Context var0, String var1) {
        return getString(var0, var1, (String)null);
    }

    public static String getString(Context var0, String var1, String var2) {
        return var0.getSharedPreferences(PREFERENCE_NAME, 0).getString(var1, var2);
    }

    public static boolean putInt(Context var0, String var1, int var2) {
        Editor var3;
        (var3 = var0.getSharedPreferences(PREFERENCE_NAME, 0).edit()).putInt(var1, var2);
        return var3.commit();
    }

    public static int getInt(Context var0, String var1) {
        return getInt(var0, var1, -1);
    }

    public static int getInt(Context var0, String var1, int var2) {
        return var0.getSharedPreferences(PREFERENCE_NAME, 0).getInt(var1, var2);
    }

    public static boolean putLong(Context var0, String var1, long var2) {
        Editor var4;
        (var4 = var0.getSharedPreferences(PREFERENCE_NAME, 0).edit()).putLong(var1, var2);
        return var4.commit();
    }

    public static long getLong(Context var0, String var1) {
        return getLong(var0, var1, -1L);
    }

    public static long getLong(Context var0, String var1, long var2) {
        return var0.getSharedPreferences(PREFERENCE_NAME, 0).getLong(var1, var2);
    }

    public static boolean putFloat(Context var0, String var1, float var2) {
        Editor var3;
        (var3 = var0.getSharedPreferences(PREFERENCE_NAME, 0).edit()).putFloat(var1, var2);
        return var3.commit();
    }

    public static float getFloat(Context var0, String var1) {
        return getFloat(var0, var1, -1.0F);
    }

    public static float getFloat(Context var0, String var1, float var2) {
        return var0.getSharedPreferences(PREFERENCE_NAME, 0).getFloat(var1, var2);
    }

    public static boolean putBoolean(Context var0, String var1, boolean var2) {
        Editor var3;
        (var3 = var0.getSharedPreferences(PREFERENCE_NAME, 0).edit()).putBoolean(var1, var2);
        return var3.commit();
    }

    public static boolean getBoolean(Context var0, String var1) {
        return getBoolean(var0, var1, false);
    }

    public static boolean getBoolean(Context var0, String var1, boolean var2) {
        return var0.getSharedPreferences(PREFERENCE_NAME, 0).getBoolean(var1, var2);
    }

    public static void applyChanges(Editor var0) {
        if (applySupported()) {
            var0.apply();
        } else {
            var0.commit();
        }
    }

    public static Boolean applySupported() {
        try {
            return VERSION.SDK_INT >= 9 ? true : false;
        } catch (NoClassDefFoundError var0) {
            return false;
        }
    }
}
