package com.alumni.connect.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alumni.connect.R;
import com.alumni.connect.data.model.Event;
import com.alumni.connect.data.repository.AdminRepository;
import com.alumni.connect.databinding.FragmentEventManagementBinding;
import com.alumni.connect.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class EventManagementFragment extends Fragment {
    private FragmentEventManagementBinding binding;
    private AdminRepository adminRepository;
    private AdminEventAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adminRepository = new AdminRepository(requireContext());

        adapter = new AdminEventAdapter();
        binding.rvEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvEvents.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadEvents);

        binding.fabAddEvent.setOnClickListener(v -> 
            Navigation.findNavController(requireView()).navigate(R.id.action_events_to_create)
        );

        loadEvents();
    }

    private void loadEvents() {
        adminRepository.getAllEvents().observe(getViewLifecycleOwner(), resource -> {
            binding.swipeRefresh.setRefreshing(resource.status == Resource.Status.LOADING);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                adapter.setEvents(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                adapter.setEvents(createMockEvents());
            }
        });
    }

    private List<Event> createMockEvents() {
        List<Event> list = new ArrayList<>();
        Event e1 = new Event();
        e1.setId("evt-1");
        e1.setTitle("Annual Alumni Summit 2026");
        e1.setEventDate("October 25, 2026");
        e1.setEventTime("10:00 AM");
        e1.setLocationType("On-Campus");
        e1.setLocationDetails("Main Auditorium");
        e1.setDescription("Annual meeting for all alumni and final year students.");

        Event e2 = new Event();
        e2.setId("evt-2");
        e2.setTitle("Tech Talk: Career in Android Development");
        e2.setEventDate("November 05, 2026");
        e2.setEventTime("02:00 PM");
        e2.setLocationType("Online");
        e2.setLocationDetails("Google Meet");
        e2.setDescription("Android developer alumni sharing transition advice.");

        list.add(e1);
        list.add(e2);
        return list;
    }

    class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.ViewHolder> {
        private List<Event> list = new ArrayList<>();

        public void setEvents(List<Event> events) {
            this.list = events;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Event event = list.get(position);
            holder.tvTitle.setText(event.getTitle());
            holder.tvDateTime.setText(event.getEventDate() + " at " + event.getEventTime());
            holder.tvLocation.setText(event.getLocationDetails() + " (" + event.getLocationType() + ")");
            holder.tvDescription.setText(event.getDescription());
            
            holder.btnRSVP.setText("Delete Event");
            holder.btnRSVP.setBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.error));
            holder.btnRSVP.setOnClickListener(v -> {
                adminRepository.deleteEvent(event.getId()).observe(getViewLifecycleOwner(), res -> {
                    if (res.status == Resource.Status.SUCCESS) {
                        Toast.makeText(requireContext(), "Event deleted successfully!", Toast.LENGTH_SHORT).show();
                        loadEvents();
                    }
                });
            });

            if (holder.btnEdit != null) {
                holder.btnEdit.setVisibility(View.VISIBLE);
                holder.btnEdit.setOnClickListener(v -> {
                    EditContentDialogFragment dialog = EditContentDialogFragment.newInstance(
                            "event", event.getId(), event.getTitle(), event.getDescription()
                    );
                    dialog.setOnContentUpdatedListener(EventManagementFragment.this::loadEvents);
                    dialog.show(getChildFragmentManager(), "EditEventDialog");
                });
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDateTime, tvLocation, tvDescription;
            Button btnRSVP, btnEdit;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvEventTitle);
                tvDateTime = itemView.findViewById(R.id.tvEventDateTime);
                tvLocation = itemView.findViewById(R.id.tvLocation);
                tvDescription = itemView.findViewById(R.id.tvEventDescription);
                btnRSVP = itemView.findViewById(R.id.btnRSVP);
                btnEdit = itemView.findViewById(R.id.btnEditEvent);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
