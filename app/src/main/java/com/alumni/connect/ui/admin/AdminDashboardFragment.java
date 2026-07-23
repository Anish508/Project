package com.alumni.connect.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.data.model.Post;
import com.alumni.connect.databinding.FragmentAdminDashboardBinding;
import com.alumni.connect.util.Resource;

public class AdminDashboardFragment extends Fragment {
    private FragmentAdminDashboardBinding binding;
    private AdminViewModel viewModel;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        sessionManager = new SessionManager(requireContext());

        binding.btnPublishPost.setOnClickListener(v -> publishBroadcast());
    }

    private void publishBroadcast() {
        String title = binding.etPostTitle.getText() != null ? binding.etPostTitle.getText().toString().trim() : "";
        String content = binding.etPostContent.getText() != null ? binding.etPostContent.getText().toString().trim() : "";

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        Post post = new Post();
        post.setAuthorId(sessionManager.getUserId());
        post.setTitle(title);
        post.setContent(content);
        post.setPostType("announcement");

        viewModel.publishAnnouncement(post).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Broadcast published successfully!", Toast.LENGTH_SHORT).show();
                binding.etPostTitle.setText("");
                binding.etPostContent.setText("");
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), "Announcement broadcast published!", Toast.LENGTH_SHORT).show();
                binding.etPostTitle.setText("");
                binding.etPostContent.setText("");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
