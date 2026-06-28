package com.example.gallery;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "app_settings";
    private static final String KEY_LOG_SCROLL = "log_scroll_visible";
    private final SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLogScrollVisible(boolean isVisible) {
        sharedPreferences.edit().putBoolean(KEY_LOG_SCROLL, isVisible).apply();
    }

    public boolean isLogScrollVisible() {
        return sharedPreferences.getBoolean(KEY_LOG_SCROLL, true);
    }
}

