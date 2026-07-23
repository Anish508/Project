package com.alumni.connect.ui.directory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alumni.connect.R;
import com.alumni.connect.data.model.AlumniProfile;
import com.alumni.connect.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(AlumniProfile profile);
    }

    private List<AlumniProfile> profiles = new ArrayList<>();
    private OnItemClickListener listener;

    public void setProfiles(List<AlumniProfile> profiles, OnItemClickListener listener) {
        this.profiles = profiles != null ? profiles : new ArrayList<>();
        this.listener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumni, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlumniProfile profile = profiles.get(position);
        User user = profile.getUser();
        String name = user != null && user.getFullName() != null ? user.getFullName() : "Alumni Member";
        holder.tvName.setText(name);

        String company = profile.getCurrentCompany() != null ? profile.getCurrentCompany() : "University Alumni";
        String designation = profile.getDesignation() != null ? profile.getDesignation() : "Professional";
        holder.tvCompanyDesignation.setText(designation + " @ " + company);

        String dept = profile.getDepartment() != null ? profile.getDepartment() : "Engineering";
        holder.tvDeptYear.setText(dept + " • Class of " + profile.getGraduationYear());

        if (profile.isAvailableForMentorship()) {
            holder.tvMentorshipStatus.setVisibility(View.VISIBLE);
            holder.tvMentorshipStatus.setText("• Mentorship Available");
        } else {
            holder.tvMentorshipStatus.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(profile);
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCompanyDesignation, tvDeptYear, tvMentorshipStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCompanyDesignation = itemView.findViewById(R.id.tvCompanyDesignation);
            tvDeptYear = itemView.findViewById(R.id.tvDeptYear);
            tvMentorshipStatus = itemView.findViewById(R.id.tvMentorshipStatus);
        }
    }
}
