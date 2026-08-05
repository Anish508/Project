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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    public interface OnEventClickListener {
        void onRsvp(Event event);
        void onViewAttendees(Event event);
    }

    private List<Event> events = new ArrayList<>();
    private Set<String> registeredEventIds = new HashSet<>();
    private OnEventClickListener listener;
    private boolean showAttendeesOption = false;
    // Admin users cannot RSVP to events
    private boolean isAdmin = false;

    public void setEvents(List<Event> events) {
        setEvents(events, new HashSet<>(), false, null);
    }

    public void setEvents(List<Event> events, Set<String> registeredEventIds, boolean showAttendeesOption, OnEventClickListener listener) {
        setEvents(events, registeredEventIds, showAttendeesOption, listener, false);
    }

    public void setEvents(List<Event> events, Set<String> registeredEventIds, boolean showAttendeesOption, OnEventClickListener listener, boolean isAdmin) {
        this.events = events != null ? events : new ArrayList<>();
        this.registeredEventIds = registeredEventIds != null ? registeredEventIds : new HashSet<>();
        this.showAttendeesOption = showAttendeesOption;
        this.listener = listener;
        this.isAdmin = isAdmin;
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

        if (isAdmin) {
            // Admin: hide RSVP, show "Admin View" badge
            holder.btnRSVP.setVisibility(View.GONE);
            if (holder.tvAdminEventLabel != null) {
                holder.tvAdminEventLabel.setVisibility(View.VISIBLE);
                holder.tvAdminEventLabel.setText("🔒 Admin View");
            }
        } else if (isRegistered) {
            if (holder.tvAdminEventLabel != null) holder.tvAdminEventLabel.setVisibility(View.GONE);
            holder.btnRSVP.setVisibility(View.VISIBLE);
            holder.btnRSVP.setText("RSVP Confirmed ✓");
            holder.btnRSVP.setEnabled(false);
        } else {
            if (holder.tvAdminEventLabel != null) holder.tvAdminEventLabel.setVisibility(View.GONE);
            holder.btnRSVP.setVisibility(View.VISIBLE);
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
        TextView tvTitle, tvDateTime, tvLocation, tvDescription, tvAdminEventLabel;
        Button btnRSVP;
        View btnViewAttendees;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDateTime = itemView.findViewById(R.id.tvEventDateTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDescription = itemView.findViewById(R.id.tvEventDescription);
            tvAdminEventLabel = itemView.findViewById(R.id.tvRsvpCount); // reuse this slot
            btnRSVP = itemView.findViewById(R.id.btnRSVP);
            btnViewAttendees = itemView.findViewById(R.id.btnViewAttendees);
        }
    }
}
