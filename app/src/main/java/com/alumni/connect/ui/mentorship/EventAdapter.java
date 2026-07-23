package com.alumni.connect.ui.mentorship;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alumni.connect.R;
import com.alumni.connect.data.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events = new ArrayList<>();

    public void setEvents(List<Event> events) {
        this.events = events != null ? events : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvTitle.setText(event.getTitle());
        holder.tvDateTime.setText("Date: " + event.getEventDate() + " • " + event.getEventTime());
        holder.tvLocation.setText("Location: " + event.getLocationDetails() + " (" + event.getLocationType() + ")");
        holder.tvDescription.setText(event.getDescription());

        holder.btnRSVP.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "RSVP confirmed for " + event.getTitle(), Toast.LENGTH_SHORT).show();
            holder.btnRSVP.setText("RSVP Confirmed");
            holder.btnRSVP.setEnabled(false);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDateTime, tvLocation, tvDescription;
        Button btnRSVP;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDateTime = itemView.findViewById(R.id.tvEventDateTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDescription = itemView.findViewById(R.id.tvEventDescription);
            btnRSVP = itemView.findViewById(R.id.btnRSVP);
        }
    }
}
