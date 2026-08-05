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
        adapter.setOnPostClickListener(post -> {
            PostDetailDialogFragment dialog = PostDetailDialogFragment.newInstance(post);
            dialog.show(getChildFragmentManager(), "post_detail_dialog");
        });

        binding.rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPosts.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::refreshData);

        refreshData();
    }

    private void refreshData() {
        loadPosts();
        loadLiveMetrics();
    }

    private void loadPosts() {
        viewModel.getPosts().observe(getViewLifecycleOwner(), resource -> {
            binding.swipeRefresh.setRefreshing(resource.status == Resource.Status.LOADING);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                List<Post> publicPosts = new ArrayList<>();
                for (Post p : resource.data) {
                    if (p.getPostType() == null || !p.getPostType().toLowerCase().startsWith("chat")) {
                        publicPosts.add(p);
                    }
                }
                adapter.setPosts(publicPosts);
            } else if (resource.status == Resource.Status.ERROR) {
                List<Post> welcomePosts = new ArrayList<>();
                Post p1 = new Post();
                p1.setTitle("Welcome to University Alumni Portal!");
                p1.setContent("Connect with fellow alumni, find career mentorship, browse job postings, and join university events.");
                p1.setPostType("announcement");
                welcomePosts.add(p1);
                adapter.setPosts(welcomePosts);
            }
        });
    }

    private void loadLiveMetrics() {
        viewModel.getAlumniProfiles().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                int count = resource.data.size();
                binding.tvStatAlumniCount.setText(String.valueOf(count));
                binding.tvStatMentorsCount.setText(String.valueOf(count));
            } else {
                binding.tvStatAlumniCount.setText("0");
                binding.tvStatMentorsCount.setText("0");
            }
        });

        viewModel.getJobs().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.tvStatJobsCount.setText(String.valueOf(resource.data.size()));
            } else {
                binding.tvStatJobsCount.setText("0");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
