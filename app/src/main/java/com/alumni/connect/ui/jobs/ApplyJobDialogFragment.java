package com.alumni.connect.ui.jobs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.data.model.Job;
import com.alumni.connect.data.model.JobApplication;
import com.alumni.connect.data.repository.JobApplicationRepository;
import com.alumni.connect.databinding.DialogApplyJobBinding;
import com.alumni.connect.util.Resource;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ApplyJobDialogFragment extends BottomSheetDialogFragment {
    private DialogApplyJobBinding binding;
    private Job job;
    private JobApplicationRepository repository;
    private SessionManager sessionManager;

    public static ApplyJobDialogFragment newInstance(Job job) {
        ApplyJobDialogFragment fragment = new ApplyJobDialogFragment();
        fragment.job = job;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogApplyJobBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (job == null) {
            dismiss();
            return;
        }

        repository = new JobApplicationRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        // Restrictions: Job poster & Admin cannot apply to the job
        String currentUserId = sessionManager.getUserId();
        String userRole = sessionManager.getRole();
        if (com.alumni.connect.util.Constants.ROLE_ADMIN.equalsIgnoreCase(userRole)) {
            Toast.makeText(requireContext(), "Admins cannot apply to job postings.", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }
        if (job.getPostedBy() != null && !currentUserId.isEmpty() && job.getPostedBy().equalsIgnoreCase(currentUserId)) {
            Toast.makeText(requireContext(), "You posted this job listing and cannot apply to it.", Toast.LENGTH_LONG).show();
            dismiss();
            return;
        }

        binding.tvJobTitleHeader.setText("Apply for " + job.getTitle());
        binding.tvCompanySubheader.setText(job.getCompany() + " • " + job.getLocation());

        // Pre-fill user session details
        binding.etApplicantName.setText(sessionManager.getFullName());
        binding.etApplicantEmail.setText(sessionManager.getEmail());

        binding.btnOpenJobLink.setOnClickListener(v -> openExternalJobLink());
        binding.btnCancelApplication.setOnClickListener(v -> dismiss());
        binding.btnSubmitApplication.setOnClickListener(v -> submitJobApplication());
    }

    private void openExternalJobLink() {
        String url = job.getApplicationLink();
        if (url == null || url.trim().isEmpty()) {
            url = "https://www.google.com/search?q=" + Uri.encode(job.getCompany() + " careers " + job.getTitle());
        } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Unable to open job platform link.", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitJobApplication() {
        String name = binding.etApplicantName.getText() != null ? binding.etApplicantName.getText().toString().trim() : "";
        String email = binding.etApplicantEmail.getText() != null ? binding.etApplicantEmail.getText().toString().trim() : "";
        String phone = binding.etApplicantPhone.getText() != null ? binding.etApplicantPhone.getText().toString().trim() : "";
        String resume = binding.etResumeUrl.getText() != null ? binding.etResumeUrl.getText().toString().trim() : "";
        String coverNote = binding.etCoverNote.getText() != null ? binding.etCoverNote.getText().toString().trim() : "";

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your name and email", Toast.LENGTH_SHORT).show();
            return;
        }

        JobApplication app = new JobApplication();
        app.setJobId(job.getId());
        app.setApplicantId(sessionManager.getUserId());
        app.setApplicantName(name);
        app.setApplicantEmail(email);
        app.setPhone(phone);
        app.setResumeUrl(resume);
        app.setCoverNote(coverNote);
        app.setPostedBy(job.getPostedBy());

        binding.pbSubmitting.setVisibility(View.VISIBLE);
        binding.btnSubmitApplication.setEnabled(false);

        repository.submitApplication(app).observe(getViewLifecycleOwner(), resource -> {
            binding.pbSubmitting.setVisibility(View.GONE);
            binding.btnSubmitApplication.setEnabled(true);

            if (resource.status == Resource.Status.SUCCESS) {
                com.alumni.connect.util.NotificationHelper.showNotification(
                        requireContext(),
                        "Job Application Submitted",
                        "✅ Application sent for " + job.getTitle() + " at " + job.getCompany()
                );
                Toast.makeText(requireContext(), "Application submitted successfully to " + job.getCompany() + "!", Toast.LENGTH_LONG).show();
                sendNotificationEmail(name, email, phone, resume, coverNote);
                dismiss();
            } else {
                Toast.makeText(requireContext(), resource.message != null ? resource.message : "Failed to submit application", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotificationEmail(String name, String email, String phone, String resume, String coverNote) {
        String recipient = job.getApplicationEmail() != null && !job.getApplicationEmail().isEmpty()
                ? job.getApplicationEmail()
                : "careers@" + job.getCompany().toLowerCase().replaceAll(" ", "") + ".com";

        String subject = "Job Application: " + job.getTitle() + " - " + name;
        String body = "Dear Hiring Manager,\n\n" +
                "I am applying for the position of " + job.getTitle() + " at " + job.getCompany() + ".\n\n" +
                "Applicant Details:\n" +
                "- Name: " + name + "\n" +
                "- Email: " + email + "\n" +
                "- Phone: " + phone + "\n" +
                "- Resume/Portfolio: " + resume + "\n\n" +
                "Cover Note:\n" + coverNote + "\n\n" +
                "Sent via Alumni Connect Platform.";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + recipient));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(intent);
        } catch (Exception ignored) {}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
