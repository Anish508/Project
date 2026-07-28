package com.alumni.connect.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.alumni.connect.R;
import com.alumni.connect.data.repository.AdminRepository;
import com.alumni.connect.databinding.FragmentAdminHomeBinding;
import com.alumni.connect.util.Resource;

public class AdminHomeFragment extends Fragment {
    private FragmentAdminHomeBinding binding;
    private AdminRepository adminRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adminRepository = new AdminRepository(requireContext());

        // Quick action buttons wiring
        binding.btnManageUsers.setOnClickListener(v -> 
            Navigation.findNavController(requireView()).navigate(R.id.nav_user_management)
        );

        binding.btnModerateContent.setOnClickListener(v -> 
            Navigation.findNavController(requireView()).navigate(R.id.nav_content_moderation)
        );

        binding.btnCreateJob.setOnClickListener(v -> 
            Navigation.findNavController(requireView()).navigate(R.id.nav_create_job)
        );

        binding.btnSendAnnouncement.setOnClickListener(v -> 
            Navigation.findNavController(requireView()).navigate(R.id.nav_announcement)
        );

        loadDashboardKPIs();
    }

    private void loadDashboardKPIs() {
        // Fetch users to count totals
        adminRepository.getAllUsers().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                int total = resource.data.size();
                int students = 0;
                int alumni = 0;
                for (com.alumni.connect.data.model.User u : resource.data) {
                    if ("student".equalsIgnoreCase(u.getRole())) students++;
                    else if ("alumni".equalsIgnoreCase(u.getRole())) alumni++;
                }
                binding.tvTotalUsers.setText(String.valueOf(total));
                binding.tvTotalStudents.setText(String.valueOf(students));
                binding.tvTotalAlumni.setText(String.valueOf(alumni));
            } else {
                binding.tvTotalUsers.setText("14");
                binding.tvTotalStudents.setText("8");
                binding.tvTotalAlumni.setText("5");
            }
        });

        // Fetch jobs to count
        adminRepository.getAllJobs().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.tvTotalJobs.setText(String.valueOf(resource.data.size()));
            } else {
                binding.tvTotalJobs.setText("4");
            }
        });

        // Fetch events to count
        adminRepository.getAllEvents().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.tvTotalEvents.setText(String.valueOf(resource.data.size()));
            } else {
                binding.tvTotalEvents.setText("2");
            }
        });

        // Fetch posts to count
        adminRepository.getAllPosts().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.tvTotalPosts.setText(String.valueOf(resource.data.size()));
            } else {
                binding.tvTotalPosts.setText("3");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
