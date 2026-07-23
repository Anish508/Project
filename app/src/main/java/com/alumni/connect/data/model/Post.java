package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("id")
    private String id;

    @SerializedName("author_id")
    private String authorId;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("post_type")
    private String postType = "discussion";

    @SerializedName("created_at")
    private String createdAt;

    public Post() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPostType() { return postType; }
    public void setPostType(String postType) { this.postType = postType; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
