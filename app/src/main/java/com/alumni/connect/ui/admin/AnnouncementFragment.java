package com.alumni.connect.ui.admin;

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
import com.alumni.connect.data.model.Announcement;
import com.alumni.connect.data.repository.AdminRepository;
import com.alumni.connect.databinding.FragmentAnnouncementBinding;
import com.alumni.connect.util.Resource;

public class AnnouncementFragment extends Fragment {
    private FragmentAnnouncementBinding binding;
    private AdminRepository adminRepository;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnnouncementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adminRepository = new AdminRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        binding.btnSendAnnouncement.setOnClickListener(v -> submitAnnouncement());
        binding.btnGenerateAi.setOnClickListener(v -> generateWithAi());
    }

    private void generateWithAi() {
        String msg = binding.etAnnouncementMessage.getText() != null ? binding.etAnnouncementMessage.getText().toString().trim() : "";
        if (msg.isEmpty()) {
            msg = binding.etAnnouncementTitle.getText() != null ? binding.etAnnouncementTitle.getText().toString().trim() : "";
        }

        if (msg.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a title or short notes first.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnGenerateAi.setEnabled(false);
        binding.btnGenerateAi.setText("Generating with Groq AI...");

        com.alumni.connect.data.repository.AiAdvisorRepository aiRepo = new com.alumni.connect.data.repository.AiAdvisorRepository();
        aiRepo.generatePostContent(msg, "official announcement broadcast").observe(getViewLifecycleOwner(), resource -> {
            binding.btnGenerateAi.setEnabled(true);
            binding.btnGenerateAi.setText("Enhance Announcement with Groq AI");

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                String fullText = resource.data;
                if (fullText.contains("TITLE:")) {
                    int titleStart = fullText.indexOf("TITLE:") + 6;
                    int lineBreak = fullText.indexOf("\n", titleStart);
                    if (lineBreak != -1) {
                        String generatedTitle = fullText.substring(titleStart, lineBreak).trim();
                        String generatedMessage = fullText.substring(lineBreak).trim();
                        binding.etAnnouncementTitle.setText(generatedTitle);
                        binding.etAnnouncementMessage.setText(generatedMessage);
                    } else {
                        binding.etAnnouncementMessage.setText(fullText);
                    }
                } else {
                    binding.etAnnouncementMessage.setText(fullText);
                }
                Toast.makeText(requireContext(), "Announcement enhanced with Groq AI!", Toast.LENGTH_SHORT).show();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), "AI Error: " + resource.message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void submitAnnouncement() {
        String title = binding.etAnnouncementTitle.getText() != null ? binding.etAnnouncementTitle.getText().toString().trim() : "";
        String message = binding.etAnnouncementMessage.getText() != null ? binding.etAnnouncementMessage.getText().toString().trim() : "";

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String target = "all";
        if (binding.rbTargetStudent.isChecked()) {
            target = "student";
        } else if (binding.rbTargetAlumni.isChecked()) {
            target = "alumni";
        }

        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setMessage(message);
        announcement.setTargetRole(target);
        announcement.setAdminId(sessionManager.getUserId());

        adminRepository.createAnnouncement(announcement).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Announcement broadcast successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), "Error: " + resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
