package com.alumni.connect.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alumni.connect.R;
import com.alumni.connect.data.model.Event;
import com.alumni.connect.data.model.EventRegistration;
import com.alumni.connect.data.model.User;
import com.alumni.connect.data.repository.EventRepository;
import com.alumni.connect.databinding.DialogViewAttendeesBinding;
import com.alumni.connect.util.Resource;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewAttendeesDialogFragment extends BottomSheetDialogFragment {
    private DialogViewAttendeesBinding binding;
    private Event event;
    private EventRepository repository;
    private AttendeeAdapter adapter;

    public static ViewAttendeesDialogFragment newInstance(Event event) {
        ViewAttendeesDialogFragment fragment = new ViewAttendeesDialogFragment();
        fragment.event = event;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogViewAttendeesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (event == null) {
            dismiss();
            return;
        }

        repository = new EventRepository(requireContext());
        binding.tvEventAttendeesTitle.setText("Attendees for " + event.getTitle());

        adapter = new AttendeeAdapter();
        binding.rvEventAttendees.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvEventAttendees.setAdapter(adapter);

        binding.btnCloseAttendees.setOnClickListener(v -> dismiss());

        loadAttendees();
    }

    private void loadAttendees() {
        if (event.getId() == null || event.getId().isEmpty()) {
            binding.tvEmptyAttendees.setVisibility(View.VISIBLE);
            return;
        }

        repository.getEventRegistrations(event.getId()).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                binding.tvEmptyAttendees.setVisibility(View.GONE);
                adapter.setRegistrations(resource.data);
            } else {
                binding.tvEmptyAttendees.setVisibility(View.VISIBLE);
                adapter.setRegistrations(new ArrayList<>());
            }
        });
    }

    static class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.ViewHolder> {
        private final List<EventRegistration> list = new ArrayList<>();

        public void setRegistrations(List<EventRegistration> registrations) {
            list.clear();
            if (registrations != null) {
                list.addAll(registrations);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            EventRegistration reg = list.get(position);
            holder.bind(reg);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvAvatar, tvName, tvEmail, tvRole, tvTime;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAvatar = itemView.findViewById(R.id.tvAttendeeAvatar);
                tvName = itemView.findViewById(R.id.tvAttendeeName);
                tvEmail = itemView.findViewById(R.id.tvAttendeeEmail);
                tvRole = itemView.findViewById(R.id.tvAttendeeRole);
                tvTime = itemView.findViewById(R.id.tvAttendeeTime);
            }

            void bind(EventRegistration reg) {
                User u = reg.getUser();
                String name = u != null && u.getFullName() != null ? u.getFullName() : "Registered Student";
                String email = u != null && u.getEmail() != null ? u.getEmail() : "user_id: " + reg.getUserId();
                String role = u != null && u.getRole() != null ? u.getRole().toUpperCase() : "ATTENDEE";

                tvName.setText(name);
                tvEmail.setText(email);
                tvRole.setText(role);
                tvAvatar.setText(name.length() > 0 ? name.substring(0, 1).toUpperCase() : "A");

                String time = reg.getCreatedAt() != null ? reg.getCreatedAt() : "Recently";
                tvTime.setText("RSVP Timestamp: " + time);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
