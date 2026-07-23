package com.alumni.connect.ui.directory;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alumni.connect.R;
import com.alumni.connect.data.model.AlumniProfile;
import com.alumni.connect.data.model.User;
import com.alumni.connect.databinding.FragmentDirectoryBinding;
import com.alumni.connect.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class DirectoryFragment extends Fragment {
    private FragmentDirectoryBinding binding;
    private DirectoryViewModel viewModel;
    private DirectoryAdapter adapter;
    private List<AlumniProfile> allProfiles = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDirectoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DirectoryViewModel.class);

        adapter = new DirectoryAdapter();
        binding.rvDirectory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDirectory.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadDirectory);

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProfiles(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadDirectory();
    }

    private void loadDirectory() {
        viewModel.getAlumniProfiles().observe(getViewLifecycleOwner(), resource -> {
            binding.swipeRefresh.setRefreshing(resource.status == Resource.Status.LOADING);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                allProfiles = resource.data;
                adapter.setProfiles(allProfiles, this::openAlumniDetail);
            } else if (resource.status == Resource.Status.ERROR) {
                // Populate mock directory entries if database empty
                allProfiles = createMockProfiles();
                adapter.setProfiles(allProfiles, this::openAlumniDetail);
            }
        });
    }

    private void openAlumniDetail(AlumniProfile profile) {
        Bundle args = new Bundle();
        args.putString("user_id", profile.getUserId());
        args.putString("name", profile.getUser() != null ? profile.getUser().getFullName() : "Alumni");
        args.putString("company", profile.getCurrentCompany());
        args.putString("designation", profile.getDesignation());
        args.putString("dept", profile.getDepartment());
        args.putInt("year", profile.getGraduationYear());
        args.putString("bio", profile.getBio());
        Navigation.findNavController(requireView()).navigate(R.id.action_directory_to_detail, args);
    }

    private void filterProfiles(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.setProfiles(allProfiles, this::openAlumniDetail);
            return;
        }
        String lower = query.toLowerCase();
        List<AlumniProfile> filtered = new ArrayList<>();
        for (AlumniProfile p : allProfiles) {
            String name = p.getUser() != null ? p.getUser().getFullName() : "";
            String company = p.getCurrentCompany() != null ? p.getCurrentCompany() : "";
            String dept = p.getDepartment() != null ? p.getDepartment() : "";
            if (name.toLowerCase().contains(lower) || company.toLowerCase().contains(lower) || dept.toLowerCase().contains(lower)) {
                filtered.add(p);
            }
        }
        adapter.setProfiles(filtered, this::openAlumniDetail);
    }

    private List<AlumniProfile> createMockProfiles() {
        List<AlumniProfile> list = new ArrayList<>();
        AlumniProfile p1 = new AlumniProfile();
        User u1 = new User("1", "priya@gmail.com", "alumni", "Priya Sharma", "");
        p1.setUser(u1);
        p1.setCurrentCompany("Google");
        p1.setDesignation("Senior Software Engineer");
        p1.setDepartment("Computer Science");
        p1.setGraduationYear(2020);
        p1.setBio("Helping CS students with career guidance, tech interview prep & cloud architecture.");
        p1.setAvailableForMentorship(true);

        AlumniProfile p2 = new AlumniProfile();
        User u2 = new User("2", "rohit@amazon.com", "alumni", "Rohit Verma", "");
        p2.setUser(u2);
        p2.setCurrentCompany("Amazon Web Services");
        p2.setDesignation("Solutions Architect");
        p2.setDepartment("Information Technology");
        p2.setGraduationYear(2019);
        p2.setBio("Passionate about distributed systems, cloud computing, and mentorship.");
        p2.setAvailableForMentorship(true);

        list.add(p1);
        list.add(p2);
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
