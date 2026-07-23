package com.alumni.connect.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alumni.connect.databinding.FragmentForgotPasswordBinding;
import com.alumni.connect.util.Resource;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnReset.setOnClickListener(v -> performReset());
        binding.tvBackToLogin.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void performReset() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.resetPassword(email).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnReset.setEnabled(false);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnReset.setEnabled(true);

                if (resource.status == Resource.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Reset email sent to " + email, Toast.LENGTH_LONG).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(requireContext(), resource.message != null ? resource.message : "Error sending reset email", Toast.LENGTH_LONG).show();
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
