package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;

public class AlumniProfile {
    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("graduation_year")
    private int graduationYear;

    @SerializedName("department")
    private String department;

    @SerializedName("current_company")
    private String currentCompany;

    @SerializedName("designation")
    private String designation;

    @SerializedName("industry")
    private String industry;

    @SerializedName("location")
    private String location;

    @SerializedName("bio")
    private String bio;

    @SerializedName("linkedin_url")
    private String linkedinUrl;

    @SerializedName("available_for_mentorship")
    private boolean availableForMentorship = true;

    // Optional joins
    @SerializedName("users")
    private User user;

    public AlumniProfile() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getGraduationYear() { return graduationYear; }
    public void setGraduationYear(int graduationYear) { this.graduationYear = graduationYear; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCurrentCompany() { return currentCompany; }
    public void setCurrentCompany(String currentCompany) { this.currentCompany = currentCompany; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public boolean isAvailableForMentorship() { return availableForMentorship; }
    public void setAvailableForMentorship(boolean availableForMentorship) { this.availableForMentorship = availableForMentorship; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
