package com.alumni.connect.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alumni.connect.R;
import com.alumni.connect.databinding.FragmentRegisterBinding;
import com.alumni.connect.ui.main.MainActivity;
import com.alumni.connect.util.Constants;
import com.alumni.connect.util.Resource;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbStudent) {
                binding.tilCompany.setVisibility(View.GONE);
                binding.tilDesignation.setVisibility(View.GONE);
                binding.tilYear.setHint("Batch Year (e.g. 2025)");
            } else if (checkedId == R.id.rbAlumni) {
                binding.tilCompany.setVisibility(View.VISIBLE);
                binding.tilDesignation.setVisibility(View.VISIBLE);
                binding.tilYear.setHint("Graduation Year (e.g. 2021)");
            } else {
                binding.tilCompany.setVisibility(View.GONE);
                binding.tilDesignation.setVisibility(View.GONE);
                binding.tilYear.setHint("Year of Joining");
            }
        });

        binding.btnRegister.setOnClickListener(v -> performRegistration());

        binding.tvLogin.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void performRegistration() {
        String fullName = binding.etFullName.getText() != null ? binding.etFullName.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
        String department = binding.etDepartment.getText() != null ? binding.etDepartment.getText().toString().trim() : "";
        String yearStr = binding.etYear.getText() != null ? binding.etYear.getText().toString().trim() : "2024";
        String company = binding.etCompany.getText() != null ? binding.etCompany.getText().toString().trim() : "";
        String designation = binding.etDesignation.getText() != null ? binding.etDesignation.getText().toString().trim() : "";

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || department.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all mandatory fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int year = 2024;
        try {
            year = Integer.parseInt(yearStr);
        } catch (Exception ignored) {}

        String role = Constants.ROLE_STUDENT;
        if (binding.rbAlumni.isChecked()) {
            role = Constants.ROLE_ALUMNI;
        } else if (binding.rbAdmin.isChecked()) {
            role = Constants.ROLE_ADMIN;
        }

        viewModel.register(email, password, fullName, role, department, year, company, designation)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (resource.status == Resource.Status.LOADING) {
                        binding.progressBar.setVisibility(View.VISIBLE);
                        binding.btnRegister.setEnabled(false);
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnRegister.setEnabled(true);

                        if (resource.status == Resource.Status.SUCCESS) {
                            Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(requireActivity(), MainActivity.class));
                            requireActivity().finish();
                        } else {
                            Toast.makeText(requireContext(), resource.message != null ? resource.message : "Registration failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
