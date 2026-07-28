package com.alumni.connect.ui.jobs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alumni.connect.R;
import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.data.model.Job;
import com.alumni.connect.databinding.FragmentJobsBinding;
import com.alumni.connect.util.Constants;
import com.alumni.connect.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class JobsFragment extends Fragment {
    private FragmentJobsBinding binding;
    private JobsViewModel viewModel;
    private JobAdapter adapter;
    private List<Job> allJobs = new ArrayList<>();
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentJobsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(JobsViewModel.class);
        sessionManager = new SessionManager(requireContext());

        adapter = new JobAdapter();
        binding.rvJobs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvJobs.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadJobs);

        // Show FAB for posting job if Alumni or Admin
        if (Constants.ROLE_STUDENT.equals(sessionManager.getRole())) {
            binding.fabPostJob.setVisibility(View.GONE);
        } else {
            binding.fabPostJob.setVisibility(View.VISIBLE);
        }

        binding.fabPostJob.setOnClickListener(v -> {
            // Check verification if Alumni
            if (Constants.ROLE_ALUMNI.equals(sessionManager.getRole())) {
                // Fetch profile to verify is_verified status
                com.alumni.connect.data.repository.ProfileRepository profileRepo = new com.alumni.connect.data.repository.ProfileRepository(requireContext());
                profileRepo.getAlumniProfileByUserId(sessionManager.getUserId()).observe(getViewLifecycleOwner(), res -> {
                    if (res != null && res.data != null && !res.data.isEmpty()) {
                        com.alumni.connect.data.model.AlumniProfile profile = res.data.get(0);
                        if (profile.getUser() != null && !profile.getUser().isVerified()) {
                            Toast.makeText(requireContext(), "Your alumni account is pending verification by the Admin. Posting jobs requires verification.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    Navigation.findNavController(requireView()).navigate(R.id.action_jobs_to_create);
                });
            } else {
                Navigation.findNavController(requireView()).navigate(R.id.action_jobs_to_create);
            }
        });

        binding.etJobSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterJobs(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadJobs();
    }

    private void loadJobs() {
        boolean canViewApps = Constants.ROLE_ADMIN.equals(sessionManager.getRole()) || Constants.ROLE_ALUMNI.equals(sessionManager.getRole());
        viewModel.getJobs().observe(getViewLifecycleOwner(), resource -> {
            binding.swipeRefresh.setRefreshing(resource.status == Resource.Status.LOADING);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                allJobs = filterJobsByAudience(resource.data);
                adapter.setJobs(allJobs, canViewApps, createJobClickListener());
            } else if (resource.status == Resource.Status.ERROR || allJobs.isEmpty()) {
                allJobs = filterJobsByAudience(createMockJobs());
                adapter.setJobs(allJobs, canViewApps, createJobClickListener());
            }
        });
    }

    private List<Job> filterJobsByAudience(List<Job> jobs) {
        String role = sessionManager.getRole();
        if (Constants.ROLE_ADMIN.equals(role)) {
            return jobs; // Admin sees all jobs
        }
        List<Job> audienceFiltered = new ArrayList<>();
        for (Job j : jobs) {
            String target = j.getTargetAudience() != null ? j.getTargetAudience().toLowerCase() : "all";
            if ("all".equals(target) || role.equalsIgnoreCase(target)) {
                audienceFiltered.add(j);
            }
        }
        return audienceFiltered;
    }

    private JobAdapter.OnJobClickListener createJobClickListener() {
        return new JobAdapter.OnJobClickListener() {
            @Override
            public void onApply(Job job) {
                ApplyJobDialogFragment.newInstance(job).show(getChildFragmentManager(), "ApplyJobDialog");
            }

            @Override
            public void onViewApplications(Job job) {
                ViewApplicationsDialogFragment.newInstance(job).show(getChildFragmentManager(), "ViewApplicationsDialog");
            }
        };
    }

    private void filterJobs(String query) {
        boolean canViewApps = Constants.ROLE_ADMIN.equals(sessionManager.getRole()) || Constants.ROLE_ALUMNI.equals(sessionManager.getRole());
        List<Job> audienceJobs = filterJobsByAudience(allJobs);

        if (query == null || query.trim().isEmpty()) {
            adapter.setJobs(audienceJobs, canViewApps, createJobClickListener());
            return;
        }
        String lower = query.toLowerCase();
        List<Job> filtered = new ArrayList<>();
        for (Job j : audienceJobs) {
            if ((j.getTitle() != null && j.getTitle().toLowerCase().contains(lower)) ||
                (j.getCompany() != null && j.getCompany().toLowerCase().contains(lower)) ||
                (j.getLocation() != null && j.getLocation().toLowerCase().contains(lower)) ||
                (j.getSkillsRequired() != null && j.getSkillsRequired().toLowerCase().contains(lower)) ||
                (j.getEligibility() != null && j.getEligibility().toLowerCase().contains(lower))) {
                filtered.add(j);
            }
        }
        adapter.setJobs(filtered, canViewApps, createJobClickListener());
    }

    private List<Job> createMockJobs() {
        List<Job> list = new ArrayList<>();
        Job j1 = new Job();
        j1.setTitle("Associate Android Developer");
        j1.setCompany("Google");
        j1.setLocation("Bangalore / Remote");
        j1.setJobType("Full-time");
        j1.setSalaryRange("$90,000 - $110,000");
        j1.setDescription("Looking for fresh graduates proficient in Java, MVVM, and REST APIs for Android development.");
        j1.setApplicationEmail("jobs@google.com");
        j1.setApplicationLink("https://careers.google.com/");

        Job j2 = new Job();
        j2.setTitle("Software Engineering Intern");
        j2.setCompany("Microsoft");
        j2.setLocation("Hyderabad");
        j2.setJobType("Internship");
        j2.setSalaryRange("₹60,000 / month");
        j2.setDescription("6-month internship program for final year CS/ECE students. Mentorship provided by senior alumni.");
        j2.setApplicationEmail("careers@microsoft.com");
        j2.setApplicationLink("https://careers.microsoft.com/");

        list.add(j1);
        list.add(j2);
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
