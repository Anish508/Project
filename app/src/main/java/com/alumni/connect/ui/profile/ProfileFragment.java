package com.alumni.connect.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.alumni.connect.R;
import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.databinding.FragmentProfileBinding;
import com.alumni.connect.ui.auth.AuthActivity;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());

        binding.tvProfileName.setText(sessionManager.getFullName());
        binding.tvProfileRole.setText(sessionManager.getRole().toUpperCase() + " MEMBER");
        binding.tvProfileEmail.setText(sessionManager.getEmail());

        binding.btnEditProfile.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_profile_to_edit);
        });

        binding.btnLogout.setOnClickListener(v -> {
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
