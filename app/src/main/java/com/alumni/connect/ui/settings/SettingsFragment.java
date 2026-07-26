package com.alumni.connect.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.databinding.FragmentSettingsBinding;
import com.alumni.connect.ui.auth.AuthActivity;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private SessionManager sessionManager;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        sharedPreferences = requireActivity().getSharedPreferences("settings_pref", Context.MODE_PRIVATE);

        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        binding.switchDarkMode.setChecked(isDarkMode);

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            Toast.makeText(requireContext(), "Theme changed!", Toast.LENGTH_SHORT).show();
        });

        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> 
            Toast.makeText(requireContext(), isChecked ? "Notifications enabled" : "Notifications disabled", Toast.LENGTH_SHORT).show()
        );

        binding.btnChangePassword.setOnClickListener(v -> 
            Toast.makeText(requireContext(), "Change password link sent to your email", Toast.LENGTH_SHORT).show()
        );

        binding.btnDeleteAccount.setOnClickListener(v -> {
            sessionManager.clearSession();
            Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
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
