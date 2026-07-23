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
    }

    private List<Job> jobs = new ArrayList<>();
    private OnJobClickListener listener;

    public void setJobs(List<Job> jobs, OnJobClickListener listener) {
        this.jobs = jobs != null ? jobs : new ArrayList<>();
        this.listener = listener;
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

        holder.btnApply.setOnClickListener(v -> {
            if (listener != null) listener.onApply(job);
        });
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCompanyLocation, tvJobType, tvSalary, tvDescription;
        Button btnApply;

        JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompanyLocation = itemView.findViewById(R.id.tvCompanyLocation);
            tvJobType = itemView.findViewById(R.id.tvJobTypeChip);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnApply = itemView.findViewById(R.id.btnApply);
        }
    }
}
