package com.alumni.connect.ui.directory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alumni.connect.R;
import com.alumni.connect.data.model.AlumniProfile;
import com.alumni.connect.data.model.StudentProfile;
import com.alumni.connect.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(DirectoryItem item);
    }

    public interface OnAlumniClickListener {
        void onItemClick(AlumniProfile profile);
    }

    public static class DirectoryItem {
        public boolean isAlumni;
        public AlumniProfile alumniProfile;
        public StudentProfile studentProfile;

        public DirectoryItem(AlumniProfile alumniProfile) {
            this.isAlumni = true;
            this.alumniProfile = alumniProfile;
        }

        public DirectoryItem(StudentProfile studentProfile) {
            this.isAlumni = false;
            this.studentProfile = studentProfile;
        }

        public User getUser() {
            if (isAlumni && alumniProfile != null) return alumniProfile.getUser();
            if (!isAlumni && studentProfile != null) return studentProfile.getUser();
            return null;
        }
    }

    private List<DirectoryItem> items = new ArrayList<>();
    private OnItemClickListener listener;

    public void setItems(List<DirectoryItem> items, OnItemClickListener listener) {
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
        notifyDataSetChanged();
    }

    public void setProfiles(List<AlumniProfile> profiles, OnAlumniClickListener legacyListener) {
        List<DirectoryItem> list = new ArrayList<>();
        if (profiles != null) {
            for (AlumniProfile ap : profiles) {
                list.add(new DirectoryItem(ap));
            }
        }
        setItems(list, legacyListener != null ? item -> {
            if (item.isAlumni) legacyListener.onItemClick(item.alumniProfile);
        } : null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumni, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DirectoryItem item = items.get(position);
        User user = item.getUser();

        if (item.isAlumni) {
            AlumniProfile profile = item.alumniProfile;
            String name = user != null && user.getFullName() != null ? user.getFullName() : "Alumni Member";
            holder.tvName.setText(name);
            if (holder.tvRoleTag != null) holder.tvRoleTag.setText("ALUMNI");

            String company = profile.getCurrentCompany() != null ? profile.getCurrentCompany() : "Tech Industry";
            String designation = profile.getDesignation() != null ? profile.getDesignation() : "Alumni Member";
            holder.tvCompanyDesignation.setText(designation + " @ " + company);

            String dept = profile.getDepartment() != null ? profile.getDepartment() : "Engineering";
            holder.tvDeptYear.setText(dept + " • Class of " + profile.getGraduationYear());

            if (profile.isAvailableForMentorship()) {
                holder.tvMentorshipStatus.setVisibility(View.VISIBLE);
                holder.tvMentorshipStatus.setText("• Mentorship Available");
            } else {
                holder.tvMentorshipStatus.setVisibility(View.GONE);
            }
        } else {
            StudentProfile profile = item.studentProfile;
            String name = user != null && user.getFullName() != null ? user.getFullName() : "Student Member";
            holder.tvName.setText(name);
            if (holder.tvRoleTag != null) holder.tvRoleTag.setText("STUDENT");

            String dept = profile.getDepartment() != null ? profile.getDepartment() : "Computer Science";
            int year = profile.getBatchYear() > 0 ? profile.getBatchYear() : 2025;
            holder.tvCompanyDesignation.setText(dept + " • Batch " + year);

            String bio = profile.getBio() != null && !profile.getBio().isEmpty() ? profile.getBio() : "Student at University";
            holder.tvDeptYear.setText(bio);

            holder.tvMentorshipStatus.setVisibility(View.VISIBLE);
            holder.tvMentorshipStatus.setText("• Student Member");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRoleTag, tvCompanyDesignation, tvDeptYear, tvMentorshipStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRoleTag = itemView.findViewById(R.id.tvRoleTag);
            tvCompanyDesignation = itemView.findViewById(R.id.tvCompanyDesignation);
            tvDeptYear = itemView.findViewById(R.id.tvDeptYear);
            tvMentorshipStatus = itemView.findViewById(R.id.tvMentorshipStatus);
        }
    }
}
