package com.alumni.connect.ui.admin;

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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alumni.connect.R;
import com.alumni.connect.data.model.User;
import com.alumni.connect.data.repository.AdminRepository;
import com.alumni.connect.databinding.FragmentUserManagementBinding;
import com.alumni.connect.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class UserManagementFragment extends Fragment {
    private FragmentUserManagementBinding binding;
    private AdminRepository adminRepository;
    private UserAdapter adapter;
    private List<User> allUsers = new ArrayList<>();
    private String currentRoleFilter = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adminRepository = new AdminRepository(requireContext());

        adapter = new UserAdapter();
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvUsers.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadUsers);

        // Search text watcher
        binding.etSearchUsers.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filter chips listeners
        binding.chipGroupRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipStudents) {
                currentRoleFilter = "student";
            } else if (checkedId == R.id.chipAlumni) {
                currentRoleFilter = "alumni";
            } else if (checkedId == R.id.chipAdmins) {
                currentRoleFilter = "admin";
            } else {
                currentRoleFilter = "All";
            }
            filterUsers(binding.etSearchUsers.getText() != null ? binding.etSearchUsers.getText().toString() : "");
        });

        loadUsers();
    }

    private void loadUsers() {
        adminRepository.getAllUsers().observe(getViewLifecycleOwner(), resource -> {
            binding.swipeRefresh.setRefreshing(resource.status == Resource.Status.LOADING);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                allUsers = resource.data;
                applyFilters();
            } else if (resource.status == Resource.Status.ERROR) {
                // Populate mock users for testing
                allUsers = createMockUsers();
                applyFilters();
            }
        });
    }

    private void filterUsers(String query) {
        applyFilters();
    }

    private void applyFilters() {
        String query = binding.etSearchUsers.getText() != null ? binding.etSearchUsers.getText().toString().trim().toLowerCase() : "";
        List<User> filtered = new ArrayList<>();
        for (User u : allUsers) {
            boolean matchesRole = "All".equalsIgnoreCase(currentRoleFilter) || currentRoleFilter.equalsIgnoreCase(u.getRole());
            boolean matchesSearch = query.isEmpty() || 
                    (u.getFullName() != null && u.getFullName().toLowerCase().contains(query)) ||
                    (u.getEmail() != null && u.getEmail().toLowerCase().contains(query));

            if (matchesRole && matchesSearch) {
                filtered.add(u);
            }
        }

        adapter.setUsers(filtered, new UserAdapter.OnUserActionListener() {
            @Override
            public void onUserClick(User user) {
                Bundle args = new Bundle();
                args.putString("user_id", user.getId());
                args.putString("name", user.getFullName());
                args.putString("email", user.getEmail());
                args.putString("role", user.getRole());
                args.putBoolean("is_verified", user.isVerified());
                args.putBoolean("is_active", user.isActive());
                Navigation.findNavController(requireView()).navigate(R.id.action_users_to_detail, args);
            }

            @Override
            public void onVerify(User user) {
                adminRepository.verifyUser(user.getId()).observe(getViewLifecycleOwner(), res -> {
                    if (res.status == Resource.Status.SUCCESS) {
                        user.setVerified(true);
                        Toast.makeText(requireContext(), user.getFullName() + " verified!", Toast.LENGTH_SHORT).show();
                        applyFilters();
                    }
                });
            }

            @Override
            public void onSuspend(User user) {
                if (user.isActive()) {
                    adminRepository.suspendUser(user.getId()).observe(getViewLifecycleOwner(), res -> {
                        if (res.status == Resource.Status.SUCCESS) {
                            user.setActive(false);
                            Toast.makeText(requireContext(), user.getFullName() + " suspended!", Toast.LENGTH_SHORT).show();
                            applyFilters();
                        }
                    });
                } else {
                    adminRepository.activateUser(user.getId()).observe(getViewLifecycleOwner(), res -> {
                        if (res.status == Resource.Status.SUCCESS) {
                            user.setActive(true);
                            Toast.makeText(requireContext(), user.getFullName() + " activated!", Toast.LENGTH_SHORT).show();
                            applyFilters();
                        }
                    });
                }
            }

            @Override
            public void onDelete(User user) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to permanently delete " + user.getFullName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            adminRepository.deleteUser(user.getId()).observe(getViewLifecycleOwner(), res -> {
                                if (res.status == Resource.Status.SUCCESS) {
                                    allUsers.remove(user);
                                    Toast.makeText(requireContext(), "User deleted!", Toast.LENGTH_SHORT).show();
                                    applyFilters();
                                }
                            });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    private List<User> createMockUsers() {
        List<User> list = new ArrayList<>();
        list.add(new User("std-1", "anish@gmail.com", "student", "Anish Kumar", ""));
        list.add(new User("std-2", "ganesh9741@gmail.com", "student", "Ganesh Gowda", ""));
        list.add(new User("alm-1", "priya@gmail.com", "alumni", "Priya Sharma", ""));
        list.add(new User("alm-2", "rohit@amazon.com", "alumni", "Rohit Verma", ""));
        list.add(new User("adm-1", "admin@college.edu", "admin", "College Administrator", ""));
        
        // Mark some as unverified/suspended for demo testing
        list.get(0).setVerified(false);
        list.get(1).setVerified(true);
        list.get(2).setVerified(true);
        list.get(3).setVerified(false);
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
