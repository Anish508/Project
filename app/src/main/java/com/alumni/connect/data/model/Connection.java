package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;

public class Connection {
    @SerializedName("id")
    private String id;

    @SerializedName("requester_id")
    private String requesterId;

    @SerializedName("receiver_id")
    private String receiverId;

    @SerializedName("status")
    private String status = "pending";

    @SerializedName("created_at")
    private String createdAt;

    public Connection() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRequesterId() { return requesterId; }
    public void setRequesterId(String requesterId) { this.requesterId = requesterId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
