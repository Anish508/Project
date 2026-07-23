package com.alumni.connect.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SupabaseConfig {
    // Default placeholders - User can update via Dialog or SharedPreferences
    private static final String DEFAULT_SUPABASE_URL = "https://ijgfsyfrrwzjusnxcxlr.supabase.co";
    private static final String DEFAULT_SUPABASE_KEY = "sb_publishable_29pbYA6sA3hKDdO8l724XA_EtpYrlpy";

    public static String getSupabaseUrl(Context context) {
        if (context == null) return DEFAULT_SUPABASE_URL;
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String url = prefs.getString(Constants.KEY_CUSTOM_SUPABASE_URL, DEFAULT_SUPABASE_URL);
        return (url != null && !url.trim().isEmpty()) ? url.trim() : DEFAULT_SUPABASE_URL;
    }

    public static String getSupabaseKey(Context context) {
        if (context == null) return DEFAULT_SUPABASE_KEY;
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        String key = prefs.getString(Constants.KEY_CUSTOM_SUPABASE_KEY, DEFAULT_SUPABASE_KEY);
        return (key != null && !key.trim().isEmpty()) ? key.trim() : DEFAULT_SUPABASE_KEY;
    }

    public static void saveConfig(Context context, String url, String key) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(Constants.KEY_CUSTOM_SUPABASE_URL, url)
                .putString(Constants.KEY_CUSTOM_SUPABASE_KEY, key)
                .apply();
    }
}
