package com.alumni.connect.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.data.model.Post;
import com.alumni.connect.databinding.FragmentHomeBinding;
import com.alumni.connect.util.Resource;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private PostAdapter adapter;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        sessionManager = new SessionManager(requireContext());

        binding.tvWelcomeUser.setText("Welcome back, " + sessionManager.getFullName() + "!");
        binding.tvUserRoleBadge.setText("Connected as " + sessionManager.getRole().toUpperCase());

        adapter = new PostAdapter();
        binding.rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPosts.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadPosts);

        loadPosts();
    }

    private void loadPosts() {
        viewModel.getPosts().observe(getViewLifecycleOwner(), resource -> {
            binding.swipeRefresh.setRefreshing(resource.status == Resource.Status.LOADING);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                adapter.setPosts(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                // Populate mock welcome posts if database empty
                List<Post> dummyPosts = new ArrayList<>();
                Post p1 = new Post();
                p1.setTitle("Welcome to University Alumni Portal 2026!");
                p1.setContent("Connect with fellow alumni, find career mentorship, browse job postings, and join university events.");
                p1.setPostType("announcement");
                dummyPosts.add(p1);
                adapter.setPosts(dummyPosts);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
