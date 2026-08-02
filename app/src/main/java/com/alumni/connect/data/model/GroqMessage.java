package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;

public class GroqMessage {
    @SerializedName("role")
    private String role;

    @SerializedName("content")
    private String content;

    public GroqMessage() {}

    public GroqMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
