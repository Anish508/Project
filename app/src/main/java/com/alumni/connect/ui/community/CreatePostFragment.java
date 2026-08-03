package com.alumni.connect.ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.data.model.Post;
import com.alumni.connect.data.repository.PostRepository;
import com.alumni.connect.databinding.FragmentCreatePostBinding;
import com.alumni.connect.util.Resource;

public class CreatePostFragment extends Fragment {
    private FragmentCreatePostBinding binding;
    private PostRepository postRepository;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postRepository = new PostRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        binding.btnSubmitPost.setOnClickListener(v -> submitPost());
        binding.btnGenerateAi.setOnClickListener(v -> generateWithAi());
    }

    private void generateWithAi() {
        String notes = binding.etPostContent.getText() != null ? binding.etPostContent.getText().toString().trim() : "";
        if (notes.isEmpty()) {
            notes = binding.etPostTitle.getText() != null ? binding.etPostTitle.getText().toString().trim() : "";
        }

        if (notes.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a few bullet points in Title or Body first.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnGenerateAi.setEnabled(false);
        binding.btnGenerateAi.setText("Generating with Groq AI...");

        com.alumni.connect.data.repository.AiAdvisorRepository aiRepo = new com.alumni.connect.data.repository.AiAdvisorRepository();
        aiRepo.generatePostContent(notes, "community announcement").observe(getViewLifecycleOwner(), resource -> {
            binding.btnGenerateAi.setEnabled(true);
            binding.btnGenerateAi.setText("Enhance / Generate with Groq AI");

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                String fullText = resource.data;
                if (fullText.contains("TITLE:")) {
                    int titleStart = fullText.indexOf("TITLE:") + 6;
                    int lineBreak = fullText.indexOf("\n", titleStart);
                    if (lineBreak != -1) {
                        String generatedTitle = fullText.substring(titleStart, lineBreak).trim();
                        String generatedContent = fullText.substring(lineBreak).trim();
                        binding.etPostTitle.setText(generatedTitle);
                        binding.etPostContent.setText(generatedContent);
                    } else {
                        binding.etPostContent.setText(fullText);
                    }
                } else {
                    binding.etPostContent.setText(fullText);
                }
                Toast.makeText(requireContext(), "Post enhanced with Groq AI!", Toast.LENGTH_SHORT).show();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), "AI Error: " + resource.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void submitPost() {
        String title = binding.etPostTitle.getText() != null ? binding.etPostTitle.getText().toString().trim() : "";
        String content = binding.etPostContent.getText() != null ? binding.etPostContent.getText().toString().trim() : "";

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setPostType("discussion");
        post.setAuthorId(sessionManager.getUserId());

        if (com.alumni.connect.util.Constants.ROLE_ALUMNI.equals(sessionManager.getRole())) {
            com.alumni.connect.data.repository.ProfileRepository profileRepo = new com.alumni.connect.data.repository.ProfileRepository(requireContext());
            profileRepo.getAlumniProfileByUserId(sessionManager.getUserId()).observe(getViewLifecycleOwner(), res -> {
                if (res != null && res.data != null && !res.data.isEmpty()) {
                    com.alumni.connect.data.model.AlumniProfile profile = res.data.get(0);
                    if (profile.getUser() != null && !profile.getUser().isVerified()) {
                        Toast.makeText(requireContext(), "Your alumni account is pending verification by the Administrator. Once verified, you can post news & updates.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                doPublishPost(post);
            });
        } else {
            doPublishPost(post);
        }
    }

    private void doPublishPost(Post post) {
        postRepository.createPost(post).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                com.alumni.connect.util.NotificationHelper.showNotification(
                        requireContext(),
                        "New Community Broadcast",
                        "📢 " + post.getTitle()
                );
                Toast.makeText(requireContext(), "Post published successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), "Failed to publish post: " + resource.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
