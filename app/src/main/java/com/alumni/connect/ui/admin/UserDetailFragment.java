package com.alumni.connect.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.alumni.connect.data.repository.AdminRepository;
import com.alumni.connect.databinding.FragmentUserDetailBinding;
import com.alumni.connect.util.Resource;

public class UserDetailFragment extends Fragment {
    private FragmentUserDetailBinding binding;
    private AdminRepository adminRepository;
    private String userId;
    private String userRole;
    private boolean isVerified;
    private boolean isActive;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adminRepository = new AdminRepository(requireContext());

        Bundle args = getArguments();
        if (args != null) {
            userId = args.getString("user_id", "");
            String name = args.getString("name", "User Details");
            String email = args.getString("email", "");
            userRole = args.getString("role", "student");
            isVerified = args.getBoolean("is_verified", true);
            isActive = args.getBoolean("is_active", true);

            binding.tvUserDetailName.setText(name);
            binding.tvUserDetailEmail.setText(email);
            binding.tvUserDetailRole.setText(userRole.toUpperCase());

            updateStatusViews();
        }

        // Setup role spinner
        String[] roles = {"student", "alumni", "admin"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRole.setAdapter(roleAdapter);
        
        // Pre-select current role
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].equalsIgnoreCase(userRole)) {
                binding.spinnerRole.setSelection(i);
                break;
            }
        }

        binding.btnDetailVerify.setOnClickListener(v -> {
            adminRepository.verifyUser(userId).observe(getViewLifecycleOwner(), res -> {
                if (res.status == Resource.Status.SUCCESS) {
                    isVerified = true;
                    updateStatusViews();
                    Toast.makeText(requireContext(), "User verified!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnDetailSuspend.setOnClickListener(v -> {
            adminRepository.suspendUser(userId).observe(getViewLifecycleOwner(), res -> {
                if (res.status == Resource.Status.SUCCESS) {
                    isActive = false;
                    updateStatusViews();
                    Toast.makeText(requireContext(), "User suspended!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnDetailActivate.setOnClickListener(v -> {
            adminRepository.activateUser(userId).observe(getViewLifecycleOwner(), res -> {
                if (res.status == Resource.Status.SUCCESS) {
                    isActive = true;
                    updateStatusViews();
                    Toast.makeText(requireContext(), "User activated!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnDetailDelete.setOnClickListener(v -> {
            adminRepository.deleteUser(userId).observe(getViewLifecycleOwner(), res -> {
                if (res.status == Resource.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "User deleted!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                }
            });
        });

        binding.btnChangeRole.setOnClickListener(v -> {
            String selectedRole = binding.spinnerRole.getSelectedItem().toString();
            adminRepository.changeUserRole(userId, selectedRole).observe(getViewLifecycleOwner(), res -> {
                if (res.status == Resource.Status.SUCCESS) {
                    userRole = selectedRole;
                    binding.tvUserDetailRole.setText(selectedRole.toUpperCase());
                    Toast.makeText(requireContext(), "Role changed to " + selectedRole, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateStatusViews() {
        String status = "";
        if (isVerified) {
            status += "Verified";
            binding.btnDetailVerify.setVisibility(View.GONE);
        } else {
            status += "Unverified";
            binding.btnDetailVerify.setVisibility(View.VISIBLE);
        }

        if (isActive) {
            status += " • Active";
            binding.btnDetailSuspend.setVisibility(View.VISIBLE);
            binding.btnDetailActivate.setVisibility(View.GONE);
        } else {
            status += " • Suspended";
            binding.btnDetailSuspend.setVisibility(View.GONE);
            binding.btnDetailActivate.setVisibility(View.VISIBLE);
        }

        binding.tvUserDetailStatus.setText(status);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
