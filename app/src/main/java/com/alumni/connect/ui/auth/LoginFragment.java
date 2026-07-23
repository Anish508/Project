package com.alumni.connect.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alumni.connect.R;
import com.alumni.connect.databinding.FragmentLoginBinding;
import com.alumni.connect.ui.main.MainActivity;
import com.alumni.connect.util.Resource;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnLogin.setOnClickListener(v -> performLogin());

        binding.tvRegister.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.authContainer, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.authContainer, new ForgotPasswordFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnConfigSupabase.setOnClickListener(v -> showSupabaseConfigDialog());
    }

    private void performLogin() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.login(email, password).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnLogin.setEnabled(false);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);

                if (resource.status == Resource.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireActivity(), MainActivity.class));
                    requireActivity().finish();
                } else {
                    Toast.makeText(requireContext(), resource.message != null ? resource.message : "Login failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showSupabaseConfigDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Configure Supabase Backend");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etUrl = new EditText(requireContext());
        etUrl.setHint("Supabase URL (e.g., https://xyz.supabase.co)");
        etUrl.setText(com.alumni.connect.util.SupabaseConfig.getSupabaseUrl(requireContext()));
        layout.addView(etUrl);

        final EditText etKey = new EditText(requireContext());
        etKey.setHint("Supabase Anon Key");
        etKey.setText(com.alumni.connect.util.SupabaseConfig.getSupabaseKey(requireContext()));
        layout.addView(etKey);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String url = etUrl.getText().toString().trim();
            String key = etKey.getText().toString().trim();
            com.alumni.connect.util.SupabaseConfig.saveConfig(requireContext(), url, key);
            Toast.makeText(requireContext(), "Supabase credentials updated!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
