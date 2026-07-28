package com.alumni.connect.ui.jobs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.alumni.connect.R;
import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.data.model.Job;
import com.alumni.connect.databinding.FragmentCreateJobBinding;
import com.alumni.connect.util.Resource;

public class CreateJobFragment extends Fragment {
    private FragmentCreateJobBinding binding;
    private JobsViewModel viewModel;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateJobBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(JobsViewModel.class);
        sessionManager = new SessionManager(requireContext());

        binding.btnSubmitJob.setOnClickListener(v -> submitJob());
    }

    private void submitJob() {
        String title = binding.etJobTitle.getText() != null ? binding.etJobTitle.getText().toString().trim() : "";
        String company = binding.etCompany.getText() != null ? binding.etCompany.getText().toString().trim() : "";
        String companyLogo = binding.etCompanyLogo.getText() != null ? binding.etCompanyLogo.getText().toString().trim() : "";
        String location = binding.etLocation.getText() != null ? binding.etLocation.getText().toString().trim() : "";
        String jobType = binding.etJobType.getText() != null ? binding.etJobType.getText().toString().trim() : "Full-time";
        String salary = binding.etSalary.getText() != null ? binding.etSalary.getText().toString().trim() : "";
        String eligibility = binding.etEligibility.getText() != null ? binding.etEligibility.getText().toString().trim() : "";
        String skillsRequired = binding.etSkillsRequired.getText() != null ? binding.etSkillsRequired.getText().toString().trim() : "";
        String experienceRequired = binding.etExperienceRequired.getText() != null ? binding.etExperienceRequired.getText().toString().trim() : "";
        String deadline = binding.etApplicationDeadline.getText() != null ? binding.etApplicationDeadline.getText().toString().trim() : "";
        String description = binding.etDescription.getText() != null ? binding.etDescription.getText().toString().trim() : "";
        String applicationLink = binding.etApplicationLink.getText() != null ? binding.etApplicationLink.getText().toString().trim() : "";
        String applyEmail = binding.etApplyEmail.getText() != null ? binding.etApplyEmail.getText().toString().trim() : "";

        if (title.isEmpty() || company.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in title, company, and description", Toast.LENGTH_SHORT).show();
            return;
        }

        String targetAudience = "all";
        int checkedAudienceId = binding.rgTargetAudience.getCheckedRadioButtonId();
        if (checkedAudienceId == R.id.rbAudienceStudents) {
            targetAudience = "student";
        } else if (checkedAudienceId == R.id.rbAudienceAlumni) {
            targetAudience = "alumni";
        }

        Job job = new Job();
        job.setPostedBy(sessionManager.getUserId());
        job.setTitle(title);
        job.setCompany(company);
        job.setCompanyLogoUrl(companyLogo);
        job.setLocation(location);
        job.setJobType(jobType);
        job.setSalaryRange(salary);
        job.setEligibility(eligibility);
        job.setSkillsRequired(skillsRequired);
        job.setExperienceRequired(experienceRequired);
        job.setApplicationDeadline(deadline);
        job.setDescription(description);
        job.setApplicationLink(applicationLink);
        job.setApplicationEmail(applyEmail);
        job.setTargetAudience(targetAudience);

        viewModel.createJob(job).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Job published successfully!", Toast.LENGTH_LONG).show();
                Navigation.findNavController(requireView()).popBackStack();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), "Job posting published!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
