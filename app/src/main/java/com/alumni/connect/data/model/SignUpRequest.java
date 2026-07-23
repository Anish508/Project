package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class SignUpRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("data")
    private Map<String, Object> data;

    public SignUpRequest(String email, String password, String fullName, String role) {
        this.email = email;
        this.password = password;
        this.data = new HashMap<>();
        this.data.put("full_name", fullName);
        this.data.put("role", role);
    }
}
