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
    public interface OnEventClickListener {
        void onRsvp(Event event);
        void onViewAttendees(Event event);
    }

    private List<Event> events = new ArrayList<>();
    private java.util.Set<String> registeredEventIds = new java.util.HashSet<>();
    private OnEventClickListener listener;
    private boolean showAttendeesOption = false;

    public void setEvents(List<Event> events) {
        setEvents(events, new java.util.HashSet<>(), false, null);
    }

    public void setEvents(List<Event> events, java.util.Set<String> registeredEventIds, boolean showAttendeesOption, OnEventClickListener listener) {
        this.events = events != null ? events : new ArrayList<>();
        this.registeredEventIds = registeredEventIds != null ? registeredEventIds : new java.util.HashSet<>();
        this.showAttendeesOption = showAttendeesOption;
        this.listener = listener;
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

        boolean isRegistered = event.getId() != null && registeredEventIds.contains(event.getId());

        if (isRegistered) {
            holder.btnRSVP.setText("RSVP Confirmed ✓");
            holder.btnRSVP.setEnabled(false);
        } else {
            holder.btnRSVP.setText("RSVP / Register");
            holder.btnRSVP.setEnabled(true);
            holder.btnRSVP.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRsvp(event);
                } else {
                    Toast.makeText(v.getContext(), "RSVP confirmed for " + event.getTitle(), Toast.LENGTH_SHORT).show();
                    holder.btnRSVP.setText("RSVP Confirmed ✓");
                    holder.btnRSVP.setEnabled(false);
                }
            });
        }

        if (holder.btnViewAttendees != null) {
            if (showAttendeesOption) {
                holder.btnViewAttendees.setVisibility(View.VISIBLE);
                holder.btnViewAttendees.setOnClickListener(v -> {
                    if (listener != null) listener.onViewAttendees(event);
                });
            } else {
                holder.btnViewAttendees.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDateTime, tvLocation, tvDescription;
        Button btnRSVP;
        View btnViewAttendees;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDateTime = itemView.findViewById(R.id.tvEventDateTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDescription = itemView.findViewById(R.id.tvEventDescription);
            btnRSVP = itemView.findViewById(R.id.btnRSVP);
            btnViewAttendees = itemView.findViewById(R.id.btnViewAttendees);
        }
    }
}
