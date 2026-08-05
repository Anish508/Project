package com.alumni.connect.ui.jobs;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alumni.connect.R;
import com.alumni.connect.data.model.JobApplication;

import java.util.ArrayList;
import java.util.List;

public class JobApplicationAdapter extends RecyclerView.Adapter<JobApplicationAdapter.ViewHolder> {
    private List<JobApplication> list = new ArrayList<>();

    public void setApplications(List<JobApplication> list) {
        this.list = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_application, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position), position + 1);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAvatar;
        private final TextView tvName;
        private final TextView tvEmailPhone;
        private final TextView tvAppliedDate;
        private final TextView tvCover;
        private final TextView tvResume;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvApplicantAvatar);
            tvName = itemView.findViewById(R.id.tvApplicantName);
            tvEmailPhone = itemView.findViewById(R.id.tvApplicantEmailPhone);
            tvAppliedDate = itemView.findViewById(R.id.tvAppliedDate);
            tvCover = itemView.findViewById(R.id.tvCoverNote);
            tvResume = itemView.findViewById(R.id.tvResumeLink);
        }

        public void bind(JobApplication app, int index) {
            String name = app.getApplicantName() != null && !app.getApplicantName().isEmpty()
                    ? app.getApplicantName() : "Applicant #" + index;
            tvName.setText(name);

            // Avatar initial
            if (tvAvatar != null) {
                tvAvatar.setText(name.substring(0, 1).toUpperCase());
            }

            // Email + Phone
            String emailPhone = app.getApplicantEmail() != null ? app.getApplicantEmail() : "";
            if (app.getPhone() != null && !app.getPhone().isEmpty()) {
                emailPhone += " • " + app.getPhone();
            }
            tvEmailPhone.setText(emailPhone);

            // Application date
            if (tvAppliedDate != null) {
                String raw = app.getCreatedAt();
                if (raw != null && raw.length() >= 10) {
                    tvAppliedDate.setText("Applied: " + raw.substring(0, 10));
                    tvAppliedDate.setVisibility(View.VISIBLE);
                } else {
                    tvAppliedDate.setVisibility(View.GONE);
                }
            }

            // Cover note
            if (app.getCoverNote() != null && !app.getCoverNote().isEmpty()) {
                tvCover.setVisibility(View.VISIBLE);
                tvCover.setText("💬 " + app.getCoverNote());
            } else {
                tvCover.setVisibility(View.GONE);
            }

            // Resume link
            if (app.getResumeUrl() != null && !app.getResumeUrl().isEmpty()) {
                tvResume.setVisibility(View.VISIBLE);
                tvResume.setText("📄 View Resume / Portfolio");
                tvResume.setOnClickListener(v -> {
                    try {
                        String url = app.getResumeUrl();
                        if (!url.startsWith("http://") && !url.startsWith("https://")) url = "https://" + url;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        v.getContext().startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(v.getContext(), "Cannot open link: " + app.getResumeUrl(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                tvResume.setVisibility(View.GONE);
            }
        }
    }
}
