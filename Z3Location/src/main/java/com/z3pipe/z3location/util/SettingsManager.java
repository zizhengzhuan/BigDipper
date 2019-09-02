package com.z3pipe.z3location.util;

import android.content.Context;

public class SettingsManager {
    private static SettingsManager instance;
    private static final String KEY_LAST_USER_NAME = "KEY_LAST_USER_NAME";
    private static final String KEY_LAST_PWD = "KEY_LAST_PWD";
    private static final String KEY_REMEMBER_PASSWORD = "KEY_REMEMBER_PASSWORD";
    private static final String KEY_AUTO_LOGIN = "KEY_AUTO_LOGIN";
    private static final String KEY_USER_ID = "KEY_USER_ID";

    private static final String KEY_BASE_IBPS_SERVER = "KEY_BASE_IBPS_SERVER";
    private static final String KEY_HBP_LOGIN = "KEY_HBP_LOGIN";

    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }

        return instance;
    }

    public void setLastUser(Context context, String username, String password, boolean doesRememberPassword) {
        PreferencesUtil.putString(context, KEY_LAST_USER_NAME, username);
        PreferencesUtil.putBoolean(context, KEY_REMEMBER_PASSWORD, doesRememberPassword);
        if(doesRememberPassword){
            PreferencesUtil.putString(context, KEY_LAST_PWD, password);
        }else{
            PreferencesUtil.putString(context, KEY_LAST_PWD, "");
        }
    }

    public String getLastUser(Context context) {
        return PreferencesUtil.getString(context, KEY_LAST_USER_NAME, "");
    }

    public String getLastPWD(Context context) {
        return PreferencesUtil.getString(context, KEY_LAST_PWD);
    }

    public boolean getRememberPassword(Context context) {
        return PreferencesUtil.getBoolean(context, KEY_REMEMBER_PASSWORD);
    }

    public void setAutoLogin(Context context, boolean b) {
        PreferencesUtil.putBoolean(context, KEY_AUTO_LOGIN, b);
    }

    public boolean getAutoLogin(Context context) {
        return PreferencesUtil.getBoolean(context, KEY_AUTO_LOGIN);
    }

    public void setHBPLogin(Context context, boolean b) {
        PreferencesUtil.putBoolean(context, KEY_HBP_LOGIN, b);
    }

    public boolean getHBPLogin(Context context) {
        return PreferencesUtil.getBoolean(context, KEY_HBP_LOGIN);
    }

    public void setHbpBaseServer(Context context, String path) {
        PreferencesUtil.putString(context, KEY_BASE_IBPS_SERVER, path);
    }

    public String getHbpBaseServer(Context context) {
        return PreferencesUtil.getString(context, KEY_BASE_IBPS_SERVER, "");
    }

    public void setUserId(Context context, String path) {
        PreferencesUtil.putString(context, KEY_USER_ID, path);
    }

    public String getUserId(Context context) {
        return PreferencesUtil.getString(context, KEY_USER_ID, "0");
    }
}
