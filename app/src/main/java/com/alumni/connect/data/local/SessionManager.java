package com.alumni.connect.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.alumni.connect.util.Constants;

public class SessionManager {
    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String userId, String email, String role, String fullName, String accessToken) {
        prefs.edit()
                .putBoolean(Constants.KEY_IS_LOGGED_IN, true)
                .putString(Constants.KEY_USER_ID, userId)
                .putString(Constants.KEY_EMAIL, email)
                .putString(Constants.KEY_ROLE, role)
                .putString(Constants.KEY_FULL_NAME, fullName)
                .putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return prefs.getString(Constants.KEY_USER_ID, "");
    }

    public String getEmail() {
        return prefs.getString(Constants.KEY_EMAIL, "");
    }

    public String getRole() {
        return prefs.getString(Constants.KEY_ROLE, Constants.ROLE_STUDENT);
    }

    public String getFullName() {
        return prefs.getString(Constants.KEY_FULL_NAME, "User");
    }

    public String getAccessToken() {
        return prefs.getString(Constants.KEY_ACCESS_TOKEN, "");
    }

    public void clearSession() {
        prefs.edit()
                .remove(Constants.KEY_IS_LOGGED_IN)
                .remove(Constants.KEY_USER_ID)
                .remove(Constants.KEY_EMAIL)
                .remove(Constants.KEY_ROLE)
                .remove(Constants.KEY_FULL_NAME)
                .remove(Constants.KEY_ACCESS_TOKEN)
                .apply();
    }
}
