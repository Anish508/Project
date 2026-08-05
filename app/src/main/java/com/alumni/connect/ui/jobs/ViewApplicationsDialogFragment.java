package com.alumni.connect.ui.jobs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alumni.connect.data.model.Job;
import com.alumni.connect.data.repository.JobApplicationRepository;
import com.alumni.connect.databinding.DialogViewApplicationsBinding;
import com.alumni.connect.util.Resource;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

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

        String jobTitle = job.getTitle() != null ? job.getTitle() : "This Job";
        binding.tvJobApplicationsTitle.setText(jobTitle);
        binding.tvJobApplicationsSubtitle.setText("Applicants for " + job.getCompany());

        // Initial state
        binding.pbApplicationsLoading.setVisibility(View.VISIBLE);
        binding.rvJobApplications.setVisibility(View.GONE);
        binding.tvEmptyApplications.setVisibility(View.GONE);
        binding.tvApplicantCount.setText("Loading…");

        adapter = new JobApplicationAdapter();
        binding.rvJobApplications.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvJobApplications.setAdapter(adapter);

        binding.btnCloseApplications.setOnClickListener(v -> dismiss());

        loadApplications();
    }

    private void loadApplications() {
        if (job.getId() == null || job.getId().isEmpty()) {
            binding.pbApplicationsLoading.setVisibility(View.GONE);
            binding.tvEmptyApplications.setVisibility(View.VISIBLE);
            binding.tvApplicantCount.setText("0 Applicants");
            return;
        }

        repository.getApplicationsForJob(job.getId()).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.LOADING) {
                binding.pbApplicationsLoading.setVisibility(View.VISIBLE);
                binding.tvApplicantCount.setText("Loading…");
                return;
            }

            binding.pbApplicationsLoading.setVisibility(View.GONE);

            if (resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                int count = resource.data.size();
                binding.tvApplicantCount.setText(count + (count == 1 ? " Applicant" : " Applicants"));
                binding.tvEmptyApplications.setVisibility(View.GONE);
                binding.rvJobApplications.setVisibility(View.VISIBLE);
                adapter.setApplications(resource.data);
            } else {
                binding.tvApplicantCount.setText("0 Applicants");
                binding.tvEmptyApplications.setVisibility(View.VISIBLE);
                binding.rvJobApplications.setVisibility(View.GONE);
                adapter.setApplications(new ArrayList<>());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
