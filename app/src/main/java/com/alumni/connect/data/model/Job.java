package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;

public class Job {
    @SerializedName("id")
    private String id;

    @SerializedName("posted_by")
    private String postedBy;

    @SerializedName("title")
    private String title;

    @SerializedName("company")
    private String company;

    @SerializedName("location")
    private String location;

    @SerializedName("job_type")
    private String jobType;

    @SerializedName("salary_range")
    private String salaryRange;

    @SerializedName("description")
    private String description;

    @SerializedName("requirements")
    private String requirements;

    @SerializedName("application_email")
    private String applicationEmail;

    @SerializedName("is_active")
    private boolean isActive = true;

    @SerializedName("created_at")
    private String createdAt;

    public Job() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getApplicationEmail() { return applicationEmail; }
    public void setApplicationEmail(String applicationEmail) { this.applicationEmail = applicationEmail; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
