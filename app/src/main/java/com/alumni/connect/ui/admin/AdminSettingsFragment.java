package com.alumni.connect.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.databinding.FragmentAdminSettingsBinding;
import com.alumni.connect.ui.auth.AuthActivity;

public class AdminSettingsFragment extends Fragment {
    private FragmentAdminSettingsBinding binding;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());

        binding.tvAdminSettingsName.setText(sessionManager.getFullName());
        binding.tvAdminSettingsEmail.setText(sessionManager.getEmail());

        binding.btnAdminLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            startActivity(new Intent(requireActivity(), AuthActivity.class));
            requireActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
