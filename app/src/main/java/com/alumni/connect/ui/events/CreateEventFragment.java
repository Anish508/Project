package com.alumni.connect.ui.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.alumni.connect.R;
import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.data.model.Event;
import com.alumni.connect.data.repository.AdminRepository;
import com.alumni.connect.databinding.FragmentCreateEventBinding;
import com.alumni.connect.util.Resource;

public class CreateEventFragment extends Fragment {
    private FragmentCreateEventBinding binding;
    private AdminRepository adminRepository;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adminRepository = new AdminRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        binding.btnCreateEvent.setOnClickListener(v -> submitEvent());
    }

    private void submitEvent() {
        String title = binding.etEventTitle.getText() != null ? binding.etEventTitle.getText().toString().trim() : "";
        String banner = binding.etEventBanner.getText() != null ? binding.etEventBanner.getText().toString().trim() : "";
        String description = binding.etEventDescription.getText() != null ? binding.etEventDescription.getText().toString().trim() : "";
        String date = binding.etEventDate.getText() != null ? binding.etEventDate.getText().toString().trim() : "";
        String time = binding.etEventTime.getText() != null ? binding.etEventTime.getText().toString().trim() : "";
        String regDeadline = binding.etRegistrationDeadline.getText() != null ? binding.etRegistrationDeadline.getText().toString().trim() : "";
        String locationType = binding.etEventLocationType.getText() != null ? binding.etEventLocationType.getText().toString().trim() : "";
        String locationDetails = binding.etEventLocationDetails.getText() != null ? binding.etEventLocationDetails.getText().toString().trim() : "";

        if (title.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String targetAudience = "all";
        int checkedAudienceId = binding.rgTargetAudience.getCheckedRadioButtonId();
        if (checkedAudienceId == R.id.rbAudienceStudents) {
            targetAudience = "student";
        } else if (checkedAudienceId == R.id.rbAudienceAlumni) {
            targetAudience = "alumni";
        }

        Event event = new Event();
        event.setTitle(title);
        event.setImageUrl(banner);
        event.setDescription(description);
        event.setEventDate(date);
        event.setEventTime(time);
        event.setRegistrationDeadline(regDeadline);
        event.setLocationType(locationType.isEmpty() ? "Online" : locationType);
        event.setLocationDetails(locationDetails.isEmpty() ? "Google Meet Link" : locationDetails);
        event.setTargetAudience(targetAudience);
        event.setCreatedBy(sessionManager.getUserId());

        adminRepository.createEvent(event).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), "Event published!", Toast.LENGTH_SHORT).show();
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
