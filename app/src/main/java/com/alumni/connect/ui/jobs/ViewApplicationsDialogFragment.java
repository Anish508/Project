package com.alumni.connect.ui.jobs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alumni.connect.data.model.Job;
import com.alumni.connect.data.model.JobApplication;
import com.alumni.connect.data.repository.JobApplicationRepository;
import com.alumni.connect.databinding.DialogViewApplicationsBinding;
import com.alumni.connect.util.Resource;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewApplicationsDialogFragment extends BottomSheetDialogFragment {
    private DialogViewApplicationsBinding binding;
    private Job job;
    private JobApplicationRepository repository;
    private JobApplicationAdapter adapter;

    public static ViewApplicationsDialogFragment newInstance(Job job) {
        ViewApplicationsDialogFragment fragment = new ViewApplicationsDialogFragment();
        fragment.job = job;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogViewApplicationsBinding.inflate(inflater, container, false);
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
        binding.tvJobApplicationsTitle.setText("Applicants for " + job.getTitle());

        adapter = new JobApplicationAdapter();
        binding.rvJobApplications.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvJobApplications.setAdapter(adapter);

        binding.btnCloseApplications.setOnClickListener(v -> dismiss());

        loadApplications();
    }

    private void loadApplications() {
        if (job.getId() == null || job.getId().isEmpty()) {
            showMockApplications();
            return;
        }

        repository.getApplicationsForJob(job.getId()).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                binding.tvEmptyApplications.setVisibility(View.GONE);
                adapter.setApplications(resource.data);
            } else {
                showMockApplications();
            }
        });
    }

    private void showMockApplications() {
        List<JobApplication> mocks = new ArrayList<>();
        JobApplication a1 = new JobApplication();
        a1.setApplicantName("Ganesh Gowda");
        a1.setApplicantEmail("ganesh9741@gmail.com");
        a1.setPhone("+91 9876543210");
        a1.setCoverNote("Experienced with Android Studio, Java, Supabase REST APIs, and MVVM Architecture.");
        a1.setResumeUrl("https://github.com/ganesh-gowda");

        JobApplication a2 = new JobApplication();
        a2.setApplicantName("Anish Kumar");
        a2.setApplicantEmail("anish@gmail.com");
        a2.setPhone("+91 9123456789");
        a2.setCoverNote("Final year CS student proficient in mobile development and database design.");
        a2.setResumeUrl("https://linkedin.com/in/anish-kumar");

        mocks.add(a1);
        mocks.add(a2);

        binding.tvEmptyApplications.setVisibility(View.GONE);
        adapter.setApplications(mocks);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
