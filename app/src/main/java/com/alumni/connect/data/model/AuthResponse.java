package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("user")
    private SupabaseUser user;

    public String getAccessToken() { return accessToken; }
    public SupabaseUser getUser() { return user; }

    public static class SupabaseUser {
        @SerializedName("id")
        private String id;

        @SerializedName("email")
        private String email;

        public String getId() { return id; }
        public String getEmail() { return email; }
    }
}
