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

    @SerializedName("company_logo_url")
    private String companyLogoUrl;

    @SerializedName("location")
    private String location;

    @SerializedName("job_type")
    private String jobType = "Full-time";

    @SerializedName("salary_range")
    private String salaryRange;

    @SerializedName("description")
    private String description;

    @SerializedName("requirements")
    private String requirements;

    @SerializedName("eligibility")
    private String eligibility;

    @SerializedName("skills_required")
    private String skillsRequired;

    @SerializedName("experience_required")
    private String experienceRequired;

    @SerializedName("application_deadline")
    private String applicationDeadline;

    @SerializedName("application_link")
    private String applicationLink;

    @SerializedName("application_email")
    private String applicationEmail;

    @SerializedName("target_audience")
    private String targetAudience = "all";

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

    public String getCompanyLogoUrl() { return companyLogoUrl; }
    public void setCompanyLogoUrl(String companyLogoUrl) { this.companyLogoUrl = companyLogoUrl; }

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

    public String getEligibility() { return eligibility; }
    public void setEligibility(String eligibility) { this.eligibility = eligibility; }

    public String getSkillsRequired() { return skillsRequired; }
    public void setSkillsRequired(String skillsRequired) { this.skillsRequired = skillsRequired; }

    public String getExperienceRequired() { return experienceRequired; }
    public void setExperienceRequired(String experienceRequired) { this.experienceRequired = experienceRequired; }

    public String getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(String applicationDeadline) { this.applicationDeadline = applicationDeadline; }

    public String getApplicationLink() { return applicationLink; }
    public void setApplicationLink(String applicationLink) { this.applicationLink = applicationLink; }

    public String getApplicationEmail() { return applicationEmail; }
    public void setApplicationEmail(String applicationEmail) { this.applicationEmail = applicationEmail; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
