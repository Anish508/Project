package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;

public class Event {
    @SerializedName("id")
    private String id;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("event_date")
    private String eventDate;

    @SerializedName("event_time")
    private String eventTime;

    @SerializedName("location_type")
    private String locationType;

    @SerializedName("location_details")
    private String locationDetails;

    @SerializedName("category")
    private String category;

    @SerializedName("image_url")
    private String imageUrl;

    public Event() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }

    public String getLocationType() { return locationType; }
    public void setLocationType(String locationType) { this.locationType = locationType; }

    public String getLocationDetails() { return locationDetails; }
    public void setLocationDetails(String locationDetails) { this.locationDetails = locationDetails; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
