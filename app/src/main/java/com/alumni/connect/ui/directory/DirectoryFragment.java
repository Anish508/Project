package com.alumni.connect.ui.directory;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alumni.connect.R;
import com.alumni.connect.data.model.AlumniProfile;
import com.alumni.connect.data.model.StudentProfile;
import com.alumni.connect.data.model.User;
import com.alumni.connect.databinding.FragmentDirectoryBinding;
import com.alumni.connect.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class DirectoryFragment extends Fragment {
    private FragmentDirectoryBinding binding;
    private DirectoryViewModel viewModel;
    private DirectoryAdapter adapter;
    private List<DirectoryAdapter.DirectoryItem> allItems = new ArrayList<>();
    private List<AlumniProfile> alumniList = new ArrayList<>();
    private List<StudentProfile> studentList = new ArrayList<>();

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
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupRole.setOnCheckedChangeListener((group, checkedId) -> applyFilters());

        loadDirectory();
    }

    private void loadDirectory() {
        binding.swipeRefresh.setRefreshing(true);
        viewModel.getAlumniProfiles().observe(getViewLifecycleOwner(), alumniResource -> {
            if (alumniResource.status == Resource.Status.SUCCESS && alumniResource.data != null) {
                alumniList = alumniResource.data;
            } else if (alumniResource.status == Resource.Status.ERROR || alumniList.isEmpty()) {
                alumniList = createMockAlumniProfiles();
            }

            viewModel.getStudentProfiles().observe(getViewLifecycleOwner(), studentResource -> {
                binding.swipeRefresh.setRefreshing(false);
                if (studentResource.status == Resource.Status.SUCCESS && studentResource.data != null) {
                    studentList = studentResource.data;
                } else if (studentResource.status == Resource.Status.ERROR || studentList.isEmpty()) {
                    studentList = createMockStudentProfiles();
                }
                combineAndFilterDirectory();
            });
        });
    }

    private void combineAndFilterDirectory() {
        allItems.clear();
        for (AlumniProfile ap : alumniList) {
            allItems.add(new DirectoryAdapter.DirectoryItem(ap));
        }
        for (StudentProfile sp : studentList) {
            allItems.add(new DirectoryAdapter.DirectoryItem(sp));
        }
        applyFilters();
    }

    private void applyFilters() {
        String query = binding.etSearch.getText() != null ? binding.etSearch.getText().toString().trim().toLowerCase() : "";
        int checkedId = binding.chipGroupRole.getCheckedChipId();

        List<DirectoryAdapter.DirectoryItem> filtered = new ArrayList<>();
        for (DirectoryAdapter.DirectoryItem item : allItems) {
            boolean matchesFilter = true;

            if (checkedId == R.id.chipAlumni) {
                matchesFilter = item.isAlumni;
            } else if (checkedId == R.id.chipStudents) {
                matchesFilter = !item.isAlumni;
            } else if (checkedId == R.id.chipMentors) {
                matchesFilter = item.isAlumni && item.alumniProfile != null && item.alumniProfile.isAvailableForMentorship();
            }

            if (!matchesFilter) continue;

            User user = item.getUser();
            String name = user != null && user.getFullName() != null ? user.getFullName().toLowerCase() : "";
            String email = user != null && user.getEmail() != null ? user.getEmail().toLowerCase() : "";
            String extra1 = "";
            String extra2 = "";

            if (item.isAlumni && item.alumniProfile != null) {
                extra1 = item.alumniProfile.getCurrentCompany() != null ? item.alumniProfile.getCurrentCompany().toLowerCase() : "";
                extra2 = item.alumniProfile.getDepartment() != null ? item.alumniProfile.getDepartment().toLowerCase() : "";
            } else if (!item.isAlumni && item.studentProfile != null) {
                extra1 = item.studentProfile.getDepartment() != null ? item.studentProfile.getDepartment().toLowerCase() : "";
                extra2 = item.studentProfile.getBio() != null ? item.studentProfile.getBio().toLowerCase() : "";
            }

            boolean matchesSearch = query.isEmpty() || name.contains(query) || email.contains(query) || extra1.contains(query) || extra2.contains(query);

            if (matchesSearch) {
                filtered.add(item);
            }
        }

        adapter.setItems(filtered, this::onItemClick);
    }

    private void onItemClick(DirectoryAdapter.DirectoryItem item) {
        if (item.isAlumni) {
            AlumniProfile profile = item.alumniProfile;
            Bundle args = new Bundle();
            args.putString("user_id", profile.getUserId());
            args.putString("name", profile.getUser() != null ? profile.getUser().getFullName() : "Alumni Member");
            args.putString("company", profile.getCurrentCompany());
            args.putString("designation", profile.getDesignation());
            args.putString("dept", profile.getDepartment());
            args.putInt("year", profile.getGraduationYear());
            args.putString("bio", profile.getBio());
            Navigation.findNavController(requireView()).navigate(R.id.action_directory_to_detail, args);
        } else {
            StudentProfile profile = item.studentProfile;
            User u = profile.getUser();
            String name = u != null ? u.getFullName() : "Student Member";
            String email = u != null ? u.getEmail() : "";
            String dept = profile.getDepartment() != null ? profile.getDepartment() : "Computer Science";
            int batch = profile.getBatchYear() > 0 ? profile.getBatchYear() : 2025;

            new AlertDialog.Builder(requireContext())
                    .setTitle(name + " (Student)")
                    .setMessage("Department: " + dept + "\nBatch Year: " + batch + "\nEmail: " + email + "\n\nBio: " + (profile.getBio() != null ? profile.getBio() : "Student member"))
                    .setPositiveButton("Send Email", (dialog, which) -> {
                        if (!email.isEmpty()) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:" + email));
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Connect via Alumni Portal");
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Close", null)
                    .show();
        }
    }

    private List<AlumniProfile> createMockAlumniProfiles() {
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

    private List<StudentProfile> createMockStudentProfiles() {
        List<StudentProfile> list = new ArrayList<>();
        StudentProfile s1 = new StudentProfile();
        User u1 = new User("std-1", "anish@gmail.com", "student", "Anish Kumar", "");
        s1.setUser(u1);
        s1.setDepartment("Computer Science");
        s1.setBatchYear(2025);
        s1.setCurrentSemester(6);
        s1.setBio("Enthusiastic Android Developer interested in Cloud Architecture & Open Source.");

        StudentProfile s2 = new StudentProfile();
        User u2 = new User("std-2", "ganesh9741@gmail.com", "student", "Ganesh Gowda", "");
        s2.setUser(u2);
        s2.setDepartment("Information Science");
        s2.setBatchYear(2026);
        s2.setCurrentSemester(4);
        s2.setBio("Passionate about Web Development, UI/UX Design, and Data Structures.");

        list.add(s1);
        list.add(s2);
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
