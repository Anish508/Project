package com.alumni.connect.ui.jobs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alumni.connect.data.repository.AiAdvisorRepository;
import com.alumni.connect.databinding.DialogAiAdvisorBinding;
import com.alumni.connect.util.Resource;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AiAdvisorDialogFragment extends BottomSheetDialogFragment {
    private DialogAiAdvisorBinding binding;
    private AiAdvisorRepository aiRepository;

    private static final String ARG_JOB_TITLE = "job_title";
    private static final String ARG_JOB_DESC = "job_desc";

    private String jobTitle = "";
    private String jobDescription = "";

    public static AiAdvisorDialogFragment newInstance(String title, String description) {
        AiAdvisorDialogFragment fragment = new AiAdvisorDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_JOB_TITLE, title);
        args.putString(ARG_JOB_DESC, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAiAdvisorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aiRepository = new AiAdvisorRepository();

        Bundle args = getArguments();
        if (args != null) {
            jobTitle = args.getString(ARG_JOB_TITLE, "Target Job Role");
            jobDescription = args.getString(ARG_JOB_DESC, "");
            binding.tvJobSubTitle.setText("Evaluating compatibility for: " + jobTitle);
        }

        binding.btnAnalyze.setOnClickListener(v -> performAnalysis());
        binding.btnClose.setOnClickListener(v -> dismiss());
        binding.btnCloseButton.setOnClickListener(v -> dismiss());

        // Perform auto analysis on launch
        performAnalysis();
    }

    private void performAnalysis() {
        String userSkills = binding.etUserSkills.getText() != null ? binding.etUserSkills.getText().toString().trim() : "";

        binding.layoutLoading.setVisibility(View.VISIBLE);
        binding.btnAnalyze.setEnabled(false);

        aiRepository.evaluateResumeMatch(jobTitle, jobDescription, userSkills).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.layoutLoading.setVisibility(View.GONE);
                binding.btnAnalyze.setEnabled(true);
                binding.tvAiOutput.setText(resource.data);
            } else if (resource.status == Resource.Status.ERROR) {
                binding.layoutLoading.setVisibility(View.GONE);
                binding.btnAnalyze.setEnabled(true);
                binding.tvAiOutput.setText("Analysis Error: " + resource.message);
                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
