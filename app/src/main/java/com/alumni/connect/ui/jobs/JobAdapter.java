package com.alumni.connect.ui.jobs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alumni.connect.R;
import com.alumni.connect.data.model.Job;

import java.util.ArrayList;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    public interface OnJobClickListener {
        void onApply(Job job);
        void onViewApplications(Job job);
        void onAiMatch(Job job);
    }

    private List<Job> jobs = new ArrayList<>();
    private OnJobClickListener listener;
    private boolean isOwnerOrAdmin = false;
    private String currentUserId = "";
    private String userRole = "";

    public void setJobs(List<Job> jobs, OnJobClickListener listener) {
        setJobs(jobs, false, listener);
    }

    public void setJobs(List<Job> jobs, boolean isOwnerOrAdmin, OnJobClickListener listener) {
        setJobs(jobs, isOwnerOrAdmin, listener, "", "");
    }

    public void setJobs(List<Job> jobs, boolean isOwnerOrAdmin, OnJobClickListener listener, String currentUserId, String userRole) {
        this.jobs = jobs != null ? jobs : new ArrayList<>();
        this.isOwnerOrAdmin = isOwnerOrAdmin;
        this.listener = listener;
        this.currentUserId = currentUserId != null ? currentUserId : "";
        this.userRole = userRole != null ? userRole : "";
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobs.get(position);
        holder.tvTitle.setText(job.getTitle());
        holder.tvCompanyLocation.setText(job.getCompany() + " • " + job.getLocation());
        holder.tvJobType.setText(job.getJobType() != null ? job.getJobType() : "Full-time");
        holder.tvSalary.setText(job.getSalaryRange() != null ? "Salary: " + job.getSalaryRange() : "Competitive Salary");
        holder.tvDescription.setText(job.getDescription());

        // Determine if this is the current user's own job posting
        boolean isOwnJob = !currentUserId.isEmpty()
                && job.getPostedBy() != null
                && job.getPostedBy().equalsIgnoreCase(currentUserId);

        // Admin users cannot apply to any job
        boolean isAdmin = "admin".equalsIgnoreCase(userRole);

        if (isAdmin) {
            // Admin: hide apply, show "Admin View" tag
            holder.btnApply.setVisibility(View.GONE);
            if (holder.tvOwnJobLabel != null) {
                holder.tvOwnJobLabel.setVisibility(View.VISIBLE);
                holder.tvOwnJobLabel.setText("🔒 Admin View");
            }
        } else if (isOwnJob) {
            // Alumni viewing their own posted job
            holder.btnApply.setVisibility(View.GONE);
            if (holder.tvOwnJobLabel != null) {
                holder.tvOwnJobLabel.setVisibility(View.VISIBLE);
                holder.tvOwnJobLabel.setText("📌 Your Listing");
            }
        } else {
            holder.btnApply.setVisibility(View.VISIBLE);
            if (holder.tvOwnJobLabel != null) {
                holder.tvOwnJobLabel.setVisibility(View.GONE);
            }
            holder.btnApply.setOnClickListener(v -> {
                if (listener != null) listener.onApply(job);
            });
        }

        // View Applications — shown to Alumni (own jobs) + Admin
        if (isOwnerOrAdmin) {
            holder.btnViewApplications.setVisibility(View.VISIBLE);
            holder.btnViewApplications.setOnClickListener(v -> {
                if (listener != null) listener.onViewApplications(job);
            });
        } else {
            holder.btnViewApplications.setVisibility(View.GONE);
        }

        if (holder.btnAiMatch != null) {
            // AI Match only shown to users for jobs they can apply to
            if (isAdmin || isOwnJob) {
                holder.btnAiMatch.setVisibility(View.GONE);
            } else {
                holder.btnAiMatch.setVisibility(View.VISIBLE);
                holder.btnAiMatch.setOnClickListener(v -> {
                    if (listener != null) listener.onAiMatch(job);
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCompanyLocation, tvJobType, tvSalary, tvDescription, tvOwnJobLabel;
        Button btnApply, btnViewApplications, btnAiMatch;

        JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompanyLocation = itemView.findViewById(R.id.tvCompanyLocation);
            tvJobType = itemView.findViewById(R.id.tvJobTypeChip);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvOwnJobLabel = itemView.findViewById(R.id.tvOwnJobLabel);
            btnApply = itemView.findViewById(R.id.btnApply);
            btnViewApplications = itemView.findViewById(R.id.btnViewApplications);
            btnAiMatch = itemView.findViewById(R.id.btnAiMatch);
        }
    }
}
