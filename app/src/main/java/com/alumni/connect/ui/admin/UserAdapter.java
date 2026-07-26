package com.alumni.connect.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alumni.connect.R;
import com.alumni.connect.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> usersList = new ArrayList<>();
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onUserClick(User user);
        void onVerify(User user);
        void onSuspend(User user);
        void onDelete(User user);
    }

    public void setUsers(List<User> users, OnUserActionListener listener) {
        this.usersList = users;
        this.listener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvEmail;
        private final TextView tvRole;
        private final TextView tvStatus;
        private final View btnVerify;
        private final View btnSuspend;
        private final ImageButton btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvRole = itemView.findViewById(R.id.tvUserRole);
            tvStatus = itemView.findViewById(R.id.tvUserStatus);
            btnVerify = itemView.findViewById(R.id.btnVerify);
            btnSuspend = itemView.findViewById(R.id.btnSuspend);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(User user, OnUserActionListener listener) {
            tvName.setText(user.getFullName());
            tvEmail.setText(user.getEmail());
            tvRole.setText(user.getRole().toUpperCase());

            String statusText = "";
            if (user.isVerified()) {
                statusText += "Verified";
                btnVerify.setVisibility(View.GONE);
            } else {
                statusText += "Unverified";
                btnVerify.setVisibility(View.VISIBLE);
            }

            if (user.isActive()) {
                statusText += " • Active";
                btnSuspend.setVisibility(View.VISIBLE);
            } else {
                statusText += " • Suspended";
                btnSuspend.setVisibility(View.GONE);
            }
            tvStatus.setText(statusText);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onUserClick(user);
            });

            btnVerify.setOnClickListener(v -> {
                if (listener != null) listener.onVerify(user);
            });

            btnSuspend.setOnClickListener(v -> {
                if (listener != null) listener.onSuspend(user);
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(user);
            });
        }
    }
}
