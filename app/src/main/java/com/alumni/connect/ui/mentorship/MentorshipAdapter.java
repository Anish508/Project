package com.alumni.connect.ui.mentorship;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alumni.connect.R;
import com.alumni.connect.data.model.MentorshipRequest;

import java.util.ArrayList;
import java.util.List;

public class MentorshipAdapter extends RecyclerView.Adapter<MentorshipAdapter.ViewHolder> {
    public interface OnMentorshipActionListener {
        void onAccept(MentorshipRequest request);
        void onReject(MentorshipRequest request);
        void onConnect(MentorshipRequest request);
    }

    private List<MentorshipRequest> requests = new ArrayList<>();
    private OnMentorshipActionListener listener;

    public void setRequests(List<MentorshipRequest> requests, OnMentorshipActionListener listener) {
        this.requests = requests != null ? requests : new ArrayList<>();
        this.listener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mentorship_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MentorshipRequest request = requests.get(position);
        holder.tvTopic.setText(request.getTopic());
        holder.tvMessage.setText(request.getMessage());
        holder.tvStatus.setText(request.getStatus() != null ? request.getStatus().toUpperCase() : "PENDING");

        String mentorName = request.getMentor() != null ? request.getMentor().getFullName() : "Mentor";
        String menteeName = request.getMentee() != null ? request.getMentee().getFullName() : "Mentee";
        if (holder.tvRequesterInfo != null) {
            holder.tvRequesterInfo.setText("Mentor: " + mentorName + " • Mentee: " + menteeName);
        }

        if ("accepted".equalsIgnoreCase(request.getStatus())) {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            holder.btnConnect.setVisibility(View.VISIBLE);
        } else if ("rejected".equalsIgnoreCase(request.getStatus())) {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            holder.btnConnect.setVisibility(View.GONE);
        } else {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
            holder.btnConnect.setVisibility(View.GONE);
        }

        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) listener.onAccept(request);
        });

        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onReject(request);
        });

        holder.btnConnect.setOnClickListener(v -> {
            if (listener != null) listener.onConnect(request);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopic, tvRequesterInfo, tvMessage, tvStatus;
        Button btnAccept, btnReject, btnConnect;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopic = itemView.findViewById(R.id.tvTopic);
            tvRequesterInfo = itemView.findViewById(R.id.tvRequesterInfo);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnConnect = itemView.findViewById(R.id.btnConnect);
        }
    }
}
