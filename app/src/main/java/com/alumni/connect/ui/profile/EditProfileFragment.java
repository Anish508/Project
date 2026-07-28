package com.alumni.connect.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.databinding.FragmentEditProfileBinding;
import com.alumni.connect.util.Resource;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {
    private FragmentEditProfileBinding binding;
    private ProfileViewModel viewModel;
    private SessionManager sessionManager;
    private String selectedImageUriStr = "";

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUriStr = uri.toString();
                    binding.civEditAvatar.setImageURI(uri);
                    binding.etAvatarUrl.setText(selectedImageUriStr);
                    Toast.makeText(requireContext(), "Profile photo selected!", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        sessionManager = new SessionManager(requireContext());

        binding.etFullName.setText(sessionManager.getFullName());

        binding.btnSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        binding.btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String fullName = binding.etFullName.getText() != null ? binding.etFullName.getText().toString().trim() : "";
        String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";
        String avatarUrl = binding.etAvatarUrl.getText() != null ? binding.etAvatarUrl.getText().toString().trim() : "";
        String company = binding.etCompany.getText() != null ? binding.etCompany.getText().toString().trim() : "";
        String designation = binding.etDesignation.getText() != null ? binding.etDesignation.getText().toString().trim() : "";
        String bio = binding.etBio.getText() != null ? binding.etBio.getText().toString().trim() : "";

        if (avatarUrl.isEmpty() && !selectedImageUriStr.isEmpty()) {
            avatarUrl = selectedImageUriStr;
        }

        if (fullName.isEmpty()) {
            Toast.makeText(requireContext(), "Full name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("full_name", fullName);
        userUpdates.put("phone", phone);
        if (!avatarUrl.isEmpty()) {
            userUpdates.put("avatar_url", avatarUrl);
        }

        viewModel.updateProfile(sessionManager.getUserId(), userUpdates).observe(getViewLifecycleOwner(), resource -> {
            sessionManager.saveSession(sessionManager.getUserId(), sessionManager.getEmail(), sessionManager.getRole(), fullName, sessionManager.getAccessToken());
            
            // Save role specific profile fields
            String role = sessionManager.getRole();
            if (com.alumni.connect.util.Constants.ROLE_ALUMNI.equals(role)) {
                Map<String, Object> alumniUpdates = new HashMap<>();
                if (!company.isEmpty()) alumniUpdates.put("current_company", company);
                if (!designation.isEmpty()) alumniUpdates.put("designation", designation);
                if (!bio.isEmpty()) alumniUpdates.put("bio", bio);
                viewModel.updateAlumniProfileDetails(sessionManager.getUserId(), alumniUpdates);
            } else if (com.alumni.connect.util.Constants.ROLE_STUDENT.equals(role)) {
                Map<String, Object> studentUpdates = new HashMap<>();
                if (!bio.isEmpty()) studentUpdates.put("bio", bio);
                viewModel.updateStudentProfileDetails(sessionManager.getUserId(), studentUpdates);
            }

            Toast.makeText(requireContext(), "Profile photo & information updated successfully!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
