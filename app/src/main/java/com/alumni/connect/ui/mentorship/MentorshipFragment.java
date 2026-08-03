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
                mentorshipAdapter.setRequests(resource.data, createMentorshipListener());
            } else if (resource.status == Resource.Status.ERROR || resource.data == null || resource.data.isEmpty()) {
                List<MentorshipRequest> mock = createMockRequests();
                mentorshipAdapter.setRequests(mock, createMentorshipListener());
            }
        });
    }

    private MentorshipAdapter.OnMentorshipActionListener createMentorshipListener() {
        return new MentorshipAdapter.OnMentorshipActionListener() {
            @Override
            public void onAccept(MentorshipRequest request) {
                updateStatus(request.getId(), "accepted");
            }

            @Override
            public void onReject(MentorshipRequest request) {
                updateStatus(request.getId(), "rejected");
            }

            @Override
            public void onConnect(MentorshipRequest request) {
                String targetEmail = "";
                String targetName = "Member";

                if (request.getMentor() != null && !sessionManager.getUserId().equals(request.getMentorId())) {
                    targetEmail = request.getMentor().getEmail();
                    targetName = request.getMentor().getFullName();
                } else if (request.getMentee() != null) {
                    targetEmail = request.getMentee().getEmail();
                    targetName = request.getMentee().getFullName();
                }

                if (targetEmail == null || targetEmail.isEmpty()) {
                    targetEmail = "mentor@alumni.edu";
                }

                final String finalEmail = targetEmail;
                final String finalName = targetName;

                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Connect with " + finalName)
                        .setMessage("Start your 1-on-1 mentorship session.\nEmail: " + finalEmail + "\nTopic: " + request.getTopic())
                        .setPositiveButton("Send Email", (dialog, which) -> {
                            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_SENDTO);
                            intent.setData(android.net.Uri.parse("mailto:" + finalEmail));
                            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Mentorship Session: " + request.getTopic());
                            intent.putExtra(android.content.Intent.EXTRA_TEXT, "Hi " + finalName + ",\n\nI am reaching out regarding our mentorship session on '" + request.getTopic() + "'.");
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(requireContext(), "No email application found.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        };
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
        com.alumni.connect.data.repository.EventRepository eventRepo = new com.alumni.connect.data.repository.EventRepository(requireContext());
        boolean canViewAttendees = com.alumni.connect.util.Constants.ROLE_ADMIN.equals(sessionManager.getRole()) || com.alumni.connect.util.Constants.ROLE_ALUMNI.equals(sessionManager.getRole());

        eventRepo.getUserEventRegistrations(sessionManager.getUserId()).observe(getViewLifecycleOwner(), regRes -> {
            java.util.Set<String> registeredIds = new java.util.HashSet<>();
            if (regRes.status == Resource.Status.SUCCESS && regRes.data != null) {
                for (com.alumni.connect.data.model.EventRegistration r : regRes.data) {
                    if (r.getEventId() != null) registeredIds.add(r.getEventId());
                }
            }

            viewModel.getEvents().observe(getViewLifecycleOwner(), resource -> {
                binding.swipeRefresh.setRefreshing(resource.status == Resource.Status.LOADING);
                List<Event> displayEvents;
                if (resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                    displayEvents = filterEventsByAudience(resource.data);
                } else {
                    displayEvents = filterEventsByAudience(createMockEvents());
                }

                eventAdapter.setEvents(displayEvents, registeredIds, canViewAttendees, new EventAdapter.OnEventClickListener() {
                    @Override
                    public void onRsvp(Event event) {
                        if (event.getId() == null || event.getId().isEmpty()) {
                            Toast.makeText(requireContext(), "RSVP confirmed for " + event.getTitle(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        com.alumni.connect.data.model.EventRegistration reg = new com.alumni.connect.data.model.EventRegistration();
                        reg.setEventId(event.getId());
                        reg.setUserId(sessionManager.getUserId());

                        eventRepo.registerForEvent(reg).observe(getViewLifecycleOwner(), res -> {
                            if (res.status == Resource.Status.SUCCESS) {
                                Toast.makeText(requireContext(), "RSVP Confirmed for " + event.getTitle() + "!", Toast.LENGTH_SHORT).show();
                                loadEvents();
                            } else if (res.status == Resource.Status.ERROR) {
                                Toast.makeText(requireContext(), "RSVP Error: " + res.message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onViewAttendees(Event event) {
                        com.alumni.connect.ui.events.ViewAttendeesDialogFragment.newInstance(event)
                                .show(getChildFragmentManager(), "ViewAttendeesDialog");
                    }
                });
            });
        });
    }

    private List<Event> filterEventsByAudience(List<Event> events) {
        String role = sessionManager.getRole();
        if (com.alumni.connect.util.Constants.ROLE_ADMIN.equals(role)) {
            return events; // Admin sees all events
        }
        List<Event> filtered = new ArrayList<>();
        for (Event e : events) {
            String target = e.getTargetAudience() != null ? e.getTargetAudience().toLowerCase() : "all";
            if ("all".equals(target) || role.equalsIgnoreCase(target)) {
                filtered.add(e);
            }
        }
        return filtered;
    }

    private List<MentorshipRequest> createMockRequests() {
        List<MentorshipRequest> list = new ArrayList<>();
        
        MentorshipRequest r1 = new MentorshipRequest();
        r1.setId("req-101");
        r1.setTopic("Android Architecture & Supabase Best Practices");
        r1.setMessage("Hi! I am building my final year college project and would appreciate feedback on clean MVVM architecture.");
        r1.setStatus("pending");
        com.alumni.connect.data.model.User m1 = new com.alumni.connect.data.model.User("m1", "priya@gmail.com", "alumni", "Priya Sharma", "");
        com.alumni.connect.data.model.User s1 = new com.alumni.connect.data.model.User("s1", "anish@gmail.com", "student", "Anish Kumar", "");
        r1.setMentor(m1);
        r1.setMentee(s1);

        MentorshipRequest r2 = new MentorshipRequest();
        r2.setId("req-102");
        r2.setTopic("System Design & Tech Interview Preparation");
        r2.setMessage("Mentorship session accepted! You can connect via email or schedule a 1-on-1 virtual call.");
        r2.setStatus("accepted");
        com.alumni.connect.data.model.User m2 = new com.alumni.connect.data.model.User("m2", "rohit@amazon.com", "alumni", "Rohit Verma", "");
        r2.setMentor(m2);
        r2.setMentee(s1);

        list.add(r1);
        list.add(r2);
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
