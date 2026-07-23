package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;

public class MentorshipRequest {
    @SerializedName("id")
    private String id;

    @SerializedName("mentor_id")
    private String mentorId;

    @SerializedName("mentee_id")
    private String menteeId;

    @SerializedName("topic")
    private String topic;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status = "pending";

    @SerializedName("created_at")
    private String createdAt;

    public MentorshipRequest() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMentorId() { return mentorId; }
    public void setMentorId(String mentorId) { this.mentorId = mentorId; }

    public String getMenteeId() { return menteeId; }
    public void setMenteeId(String menteeId) { this.menteeId = menteeId; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
