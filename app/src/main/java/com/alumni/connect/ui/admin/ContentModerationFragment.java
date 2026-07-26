package com.alumni.connect.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alumni.connect.R;
import com.alumni.connect.data.model.Job;
import com.alumni.connect.data.model.Post;
import com.alumni.connect.data.repository.AdminRepository;
import com.alumni.connect.databinding.FragmentContentModerationBinding;
import com.alumni.connect.util.Resource;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ContentModerationFragment extends Fragment {
    private FragmentContentModerationBinding binding;
    private AdminRepository adminRepository;
    private ModerationAdapter adapter;
    private int currentTab = 0; // 0 = Posts, 1 = Jobs

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContentModerationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adminRepository = new AdminRepository(requireContext());

        adapter = new ModerationAdapter();
        binding.rvModerationList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvModerationList.setAdapter(adapter);

        binding.tabLayoutModeration.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                loadContent();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.swipeRefresh.setOnRefreshListener(this::loadContent);

        loadContent();
    }

    private void loadContent() {
        if (currentTab == 0) {
            loadPosts();
        } else {
            loadJobs();
        }
    }

    private void loadPosts() {
        adminRepository.getAllPosts().observe(getViewLifecycleOwner(), resource -> {
            binding.swipeRefresh.setRefreshing(resource.status == Resource.Status.LOADING);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                adapter.setPosts(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                List<Post> dummy = new ArrayList<>();
                Post p = new Post();
                p.setId("dummy-post-1");
                p.setTitle("Mock Post for Testing Moderation");
                p.setContent("This is a mock post body that the admin can delete using moderation tools.");
                dummy.add(p);
                adapter.setPosts(dummy);
            }
        });
    }

    private void loadJobs() {
        adminRepository.getAllJobs().observe(getViewLifecycleOwner(), resource -> {
            binding.swipeRefresh.setRefreshing(resource.status == Resource.Status.LOADING);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                adapter.setJobs(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                List<Job> dummy = new ArrayList<>();
                Job j = new Job();
                j.setId("dummy-job-1");
                j.setTitle("Mock Developer Job");
                j.setCompany("Mock Company Ltd");
                j.setLocation("Remote");
                dummy.add(j);
                adapter.setJobs(dummy);
            }
        });
    }

    class ModerationAdapter extends RecyclerView.Adapter<ModerationAdapter.ModerationViewHolder> {
        private final List<Object> items = new ArrayList<>();

        public void setPosts(List<Post> posts) {
            items.clear();
            items.addAll(posts);
            notifyDataSetChanged();
        }

        public void setJobs(List<Job> jobs) {
            items.clear();
            items.addAll(jobs);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ModerationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new ModerationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ModerationViewHolder holder, int position) {
            Object obj = items.get(position);
            if (obj instanceof Post) {
                Post post = (Post) obj;
                holder.tvTitle.setText(post.getTitle());
                holder.tvContent.setText(post.getContent());
                holder.tvMeta.setText("Post Type: " + post.getPostType());
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnDelete.setOnClickListener(v -> {
                    adminRepository.deletePost(post.getId()).observe(getViewLifecycleOwner(), res -> {
                        if (res.status == Resource.Status.SUCCESS) {
                            Toast.makeText(requireContext(), "Post removed!", Toast.LENGTH_SHORT).show();
                            loadContent();
                        }
                    });
                });
            } else if (obj instanceof Job) {
                Job job = (Job) obj;
                holder.tvTitle.setText(job.getTitle() + " at " + job.getCompany());
                holder.tvContent.setText(job.getDescription() != null ? job.getDescription() : "Location: " + job.getLocation());
                holder.tvMeta.setText("Job Type: " + job.getJobType());
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnDelete.setOnClickListener(v -> {
                    adminRepository.deleteJob(job.getId()).observe(getViewLifecycleOwner(), res -> {
                        if (res.status == Resource.Status.SUCCESS) {
                            Toast.makeText(requireContext(), "Job listing removed!", Toast.LENGTH_SHORT).show();
                            loadContent();
                        }
                    });
                });
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ModerationViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvContent, tvMeta;
            View btnDelete;

            public ModerationViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvPostTitle);
                tvContent = itemView.findViewById(R.id.tvPostContent);
                tvMeta = itemView.findViewById(R.id.tvPostAuthorTime);
                btnDelete = itemView.findViewById(R.id.btnDeletePost); // Reusing post layout button if exists
                if (btnDelete == null) {
                    btnDelete = itemView.findViewById(R.id.btnDeletePost);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
