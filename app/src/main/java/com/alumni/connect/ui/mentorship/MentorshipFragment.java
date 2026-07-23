package com.alumni.connect.ui.mentorship;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.data.model.Event;
import com.alumni.connect.data.model.MentorshipRequest;
import com.alumni.connect.databinding.FragmentMentorshipBinding;
import com.alumni.connect.util.Resource;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MentorshipFragment extends Fragment {
    private FragmentMentorshipBinding binding;
    private MentorshipViewModel viewModel;
    private MentorshipAdapter mentorshipAdapter;
    private EventAdapter eventAdapter;
    private SessionManager sessionManager;
    private int currentTab = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMentorshipBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MentorshipViewModel.class);
        sessionManager = new SessionManager(requireContext());

        mentorshipAdapter = new MentorshipAdapter();
        eventAdapter = new EventAdapter();

        binding.rvMentorshipEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMentorshipEvents.setAdapter(mentorshipAdapter);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                if (currentTab == 0) {
                    binding.rvMentorshipEvents.setAdapter(mentorshipAdapter);
                    loadMentorshipRequests();
                } else {
                    binding.rvMentorshipEvents.setAdapter(eventAdapter);
                    loadEvents();
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.swipeRefresh.setOnRefreshListener(() -> {
            if (currentTab == 0) loadMentorshipRequests();
            else loadEvents();
        });

        loadMentorshipRequests();
    }

    private void loadMentorshipRequests() {
        viewModel.getMentorshipRequests(sessionManager.getUserId()).observe(getViewLifecycleOwner(), resource -> {
            binding.swipeRefresh.setRefreshing(resource.status == Resource.Status.LOADING);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                mentorshipAdapter.setRequests(resource.data, new MentorshipAdapter.OnMentorshipActionListener() {
                    @Override
                    public void onAccept(MentorshipRequest request) {
                        updateStatus(request.getId(), "accepted");
                    }

                    @Override
                    public void onReject(MentorshipRequest request) {
                        updateStatus(request.getId(), "rejected");
                    }
                });
            } else if (resource.status == Resource.Status.ERROR) {
                List<MentorshipRequest> mock = createMockRequests();
                mentorshipAdapter.setRequests(mock, new MentorshipAdapter.OnMentorshipActionListener() {
                    @Override
                    public void onAccept(MentorshipRequest request) {
                        Toast.makeText(requireContext(), "Request Accepted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReject(MentorshipRequest request) {
                        Toast.makeText(requireContext(), "Request Declined", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateStatus(String requestId, String status) {
        viewModel.updateRequestStatus(requestId, status).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Request " + status, Toast.LENGTH_SHORT).show();
                loadMentorshipRequests();
            }
        });
    }

    private void loadEvents() {
        viewModel.getEvents().observe(getViewLifecycleOwner(), resource -> {
            binding.swipeRefresh.setRefreshing(resource.status == Resource.Status.LOADING);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                eventAdapter.setEvents(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                eventAdapter.setEvents(createMockEvents());
            }
        });
    }

    private List<MentorshipRequest> createMockRequests() {
        List<MentorshipRequest> list = new ArrayList<>();
        MentorshipRequest r1 = new MentorshipRequest();
        r1.setId("req-101");
        r1.setTopic("Android Architecture & Supabase Best Practices");
        r1.setMessage("Hi! I am building my final year college project and would appreciate feedback on clean MVVM architecture.");
        r1.setStatus("pending");
        list.add(r1);
        return list;
    }

    private List<Event> createMockEvents() {
        List<Event> list = new ArrayList<>();
        Event e1 = new Event();
        e1.setTitle("Annual Alumni Summit & Tech Symposium 2026");
        e1.setEventDate("October 25, 2026");
        e1.setEventTime("10:00 AM - 04:00 PM");
        e1.setLocationType("On-Campus & Online");
        e1.setLocationDetails("University Auditorium & Youtube Live");
        e1.setDescription("Keynote talks by distinguished alumni executives, networking sessions, and student project showcase.");

        Event e2 = new Event();
        e2.setTitle("Mock Technical Interview Workshop");
        e2.setEventDate("November 05, 2026");
        e2.setEventTime("02:00 PM - 05:00 PM");
        e2.setLocationType("Online Google Meet");
        e2.setLocationDetails("Virtual Link provided upon RSVP");
        e2.setDescription("One-on-one mock coding interviews conducted by experienced alumni software engineers.");

        list.add(e1);
        list.add(e2);
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
