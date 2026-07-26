package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;

public class Announcement {
    @SerializedName("id")
    private String id;

    @SerializedName("admin_id")
    private String adminId;

    @SerializedName("title")
    private String title;

    @SerializedName("message")
    private String message;

    @SerializedName("target_role")
    private String targetRole = "all";

    @SerializedName("created_at")
    private String createdAt;

    public Announcement() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
