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
        this.list = list;
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
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvEmailPhone;
        private final TextView tvCover;
        private final TextView tvResume;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvApplicantName);
            tvEmailPhone = itemView.findViewById(R.id.tvApplicantEmailPhone);
            tvCover = itemView.findViewById(R.id.tvCoverNote);
            tvResume = itemView.findViewById(R.id.tvResumeLink);
        }

        public void bind(JobApplication app) {
            tvName.setText(app.getApplicantName());
            String emailPhone = app.getApplicantEmail() + (app.getPhone() != null && !app.getPhone().isEmpty() ? " • " + app.getPhone() : "");
            tvEmailPhone.setText(emailPhone);

            if (app.getCoverNote() != null && !app.getCoverNote().isEmpty()) {
                tvCover.setVisibility(View.VISIBLE);
                tvCover.setText("Note: " + app.getCoverNote());
            } else {
                tvCover.setVisibility(View.GONE);
            }

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
