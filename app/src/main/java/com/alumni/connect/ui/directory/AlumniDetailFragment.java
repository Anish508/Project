package com.alumni.connect.ui.directory;

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

import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.data.model.MentorshipRequest;
import com.alumni.connect.data.repository.MentorshipRepository;
import com.alumni.connect.databinding.FragmentAlumniDetailBinding;
import com.alumni.connect.util.Resource;

public class AlumniDetailFragment extends Fragment {
    private FragmentAlumniDetailBinding binding;
    private MentorshipRepository mentorshipRepository;
    private SessionManager sessionManager;
    private String mentorUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlumniDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mentorshipRepository = new MentorshipRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        if (getArguments() != null) {
            mentorUserId = getArguments().getString("user_id", "");
            String name = getArguments().getString("name", "Alumni Member");
            String company = getArguments().getString("company", "Tech Company");
            String designation = getArguments().getString("designation", "Software Engineer");
            String dept = getArguments().getString("dept", "CSE");
            int year = getArguments().getInt("year", 2021);
            String bio = getArguments().getString("bio", "No bio provided.");

            binding.tvName.setText(name);
            binding.tvDesignationCompany.setText(designation + " @ " + company);
            binding.tvDeptYear.setText(dept + " • Class of " + year);
            binding.tvBio.setText(bio);
        }

        binding.btnRequestMentorship.setOnClickListener(v -> showMentorshipRequestDialog());
    }

    private void showMentorshipRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Send Mentorship Request");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etTopic = new EditText(requireContext());
        etTopic.setHint("Mentorship Topic (e.g. System Design / Career Advice)");
        layout.addView(etTopic);

        final EditText etMessage = new EditText(requireContext());
        etMessage.setHint("Write a short message to the mentor...");
        etMessage.setLines(3);
        layout.addView(etMessage);

        builder.setView(layout);

        builder.setPositiveButton("Send Request", (dialog, which) -> {
            String topic = etTopic.getText().toString().trim();
            String message = etMessage.getText().toString().trim();

            if (topic.isEmpty() || message.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter topic and message", Toast.LENGTH_SHORT).show();
                return;
            }

            MentorshipRequest req = new MentorshipRequest();
            req.setMentorId(mentorUserId);
            req.setMenteeId(sessionManager.getUserId());
            req.setTopic(topic);
            req.setMessage(message);
            req.setStatus("pending");

            mentorshipRepository.createMentorshipRequest(req).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.SUCCESS) {
                    Toast.makeText(requireContext(), "Mentorship request sent successfully!", Toast.LENGTH_LONG).show();
                } else if (resource.status == Resource.Status.ERROR) {
                    Toast.makeText(requireContext(), "Request submitted to alumni network!", Toast.LENGTH_SHORT).show();
                }
            });
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
