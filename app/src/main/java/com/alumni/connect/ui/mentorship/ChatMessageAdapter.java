package com.alumni.connect.ui.mentorship;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alumni.connect.R;
import com.alumni.connect.data.model.Post;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private List<Post> messages = new ArrayList<>();
    private String currentUserId;

    public ChatMessageAdapter(String currentUserId) {
        this.currentUserId = currentUserId != null ? currentUserId : "";
    }

    public void setMessages(List<Post> messages) {
        this.messages = messages != null ? messages : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addMessage(Post message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post msg = messages.get(position);
        boolean isSent = currentUserId.equals(msg.getAuthorId());

        String senderName = msg.getTitle() != null ? msg.getTitle() : "User";
        String content = msg.getContent() != null ? msg.getContent() : "";
        String time = "";
        if (msg.getCreatedAt() != null && msg.getCreatedAt().length() >= 16) {
            // Show HH:MM from ISO timestamp
            time = msg.getCreatedAt().substring(11, 16);
        }

        if (isSent) {
            holder.llSent.setVisibility(View.VISIBLE);
            holder.llReceived.setVisibility(View.GONE);
            holder.tvSentMessage.setText(content);
            holder.tvSentTime.setText(time);
        } else {
            holder.llSent.setVisibility(View.GONE);
            holder.llReceived.setVisibility(View.VISIBLE);
            holder.tvReceivedSender.setText(senderName);
            holder.tvReceivedMessage.setText(content);
            holder.tvReceivedTime.setText(time);
            // Avatar initial from sender name
            if (holder.tvReceivedAvatar != null && !senderName.isEmpty()) {
                holder.tvReceivedAvatar.setText(senderName.substring(0, 1).toUpperCase());
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llSent, llReceived;
        TextView tvSentMessage, tvSentTime;
        TextView tvReceivedSender, tvReceivedAvatar, tvReceivedMessage, tvReceivedTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            llSent = itemView.findViewById(R.id.llSent);
            llReceived = itemView.findViewById(R.id.llReceived);
            tvSentMessage = itemView.findViewById(R.id.tvSentMessage);
            tvSentTime = itemView.findViewById(R.id.tvSentTime);
            tvReceivedSender = itemView.findViewById(R.id.tvReceivedSender);
            tvReceivedAvatar = itemView.findViewById(R.id.tvReceivedAvatar);
            tvReceivedMessage = itemView.findViewById(R.id.tvReceivedMessage);
            tvReceivedTime = itemView.findViewById(R.id.tvReceivedTime);
        }
    }
}
