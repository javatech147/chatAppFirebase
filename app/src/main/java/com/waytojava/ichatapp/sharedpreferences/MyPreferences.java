package com.waytojava.ichatapp.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {
    private static final String PREF_FILE_NAME = "iChatPreferences";
    public static final String IS_USER_ACCESS_HOME_PAGE = "is_user_access_home_page";
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;

    public MyPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean saveString(String key, String value) {
        return editor.putString(key, value).commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "null");
    }

    public boolean clearPreferences() {
        return editor.clear().commit();
    }

    public boolean saveBoolean(String key, boolean value) {
        return editor.putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
}
