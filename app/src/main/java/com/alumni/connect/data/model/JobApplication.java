package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;

public class JobApplication {
    @SerializedName("id")
    private String id;

    @SerializedName("job_id")
    private String jobId;

    @SerializedName("applicant_id")
    private String applicantId;

    @SerializedName("applicant_name")
    private String applicantName;

    @SerializedName("applicant_email")
    private String applicantEmail;

    @SerializedName("phone")
    private String phone;

    @SerializedName("cover_note")
    private String coverNote;

    @SerializedName("resume_url")
    private String resumeUrl;

    @SerializedName("posted_by")
    private String postedBy;

    @SerializedName("status")
    private String status = "submitted";

    @SerializedName("created_at")
    private String createdAt;

    public JobApplication() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getApplicantId() { return applicantId; }
    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getApplicantEmail() { return applicantEmail; }
    public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCoverNote() { return coverNote; }
    public void setCoverNote(String coverNote) { this.coverNote = coverNote; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
