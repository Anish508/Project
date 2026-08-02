package com.alumni.connect.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alumni.connect.data.repository.AdminRepository;
import com.alumni.connect.data.repository.AiAdvisorRepository;
import com.alumni.connect.data.repository.EventRepository;
import com.alumni.connect.data.repository.PostRepository;
import com.alumni.connect.databinding.DialogEditContentBinding;
import com.alumni.connect.util.Resource;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditContentDialogFragment extends BottomSheetDialogFragment {
    public interface OnContentUpdatedListener {
        void onUpdated();
    }

    private DialogEditContentBinding binding;
    private OnContentUpdatedListener listener;

    private static final String ARG_TYPE = "type"; // "post", "event", "announcement"
    private static final String ARG_ID = "id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_BODY = "body";

    private String contentType = "post";
    private String contentId = "";
    private String initialTitle = "";
    private String initialBody = "";

    public static EditContentDialogFragment newInstance(String type, String id, String title, String body) {
        EditContentDialogFragment fragment = new EditContentDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        args.putString(ARG_ID, id);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_BODY, body);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnContentUpdatedListener(OnContentUpdatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogEditContentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            contentType = args.getString(ARG_TYPE, "post");
            contentId = args.getString(ARG_ID, "");
            initialTitle = args.getString(ARG_TITLE, "");
            initialBody = args.getString(ARG_BODY, "");

            binding.tvEditHeader.setText("Edit " + contentType.toUpperCase());
            binding.etEditTitle.setText(initialTitle);
            binding.etEditBody.setText(initialBody);
        }

        binding.btnSave.setOnClickListener(v -> saveChanges());
        binding.btnGenerateAiEdit.setOnClickListener(v -> refineWithAi());
        binding.btnClose.setOnClickListener(v -> dismiss());
    }

    private void refineWithAi() {
        String body = binding.etEditBody.getText() != null ? binding.etEditBody.getText().toString().trim() : "";
        if (body.isEmpty()) {
            body = binding.etEditTitle.getText() != null ? binding.etEditTitle.getText().toString().trim() : "";
        }

        if (body.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter text to refine.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnGenerateAiEdit.setEnabled(false);
        binding.btnGenerateAiEdit.setText("Refining with Groq AI...");

        AiAdvisorRepository aiRepo = new AiAdvisorRepository();
        aiRepo.generatePostContent(body, contentType).observe(getViewLifecycleOwner(), resource -> {
            binding.btnGenerateAiEdit.setEnabled(true);
            binding.btnGenerateAiEdit.setText("Refine with Groq AI");

            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                String result = resource.data;
                if (result.contains("TITLE:")) {
                    int titleStart = result.indexOf("TITLE:") + 6;
                    int lineBreak = result.indexOf("\n", titleStart);
                    if (lineBreak != -1) {
                        binding.etEditTitle.setText(result.substring(titleStart, lineBreak).trim());
                        binding.etEditBody.setText(result.substring(lineBreak).trim());
                    } else {
                        binding.etEditBody.setText(result);
                    }
                } else {
                    binding.etEditBody.setText(result);
                }
                Toast.makeText(requireContext(), "Content refined with Groq AI!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChanges() {
        String newTitle = binding.etEditTitle.getText() != null ? binding.etEditTitle.getText().toString().trim() : "";
        String newBody = binding.etEditBody.getText() != null ? binding.etEditBody.getText().toString().trim() : "";

        if (newTitle.isEmpty() || newBody.isEmpty()) {
            Toast.makeText(requireContext(), "Title and Description cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnSave.setEnabled(false);

        if ("post".equalsIgnoreCase(contentType)) {
            PostRepository repo = new PostRepository(requireContext());
            repo.updatePost(contentId, newTitle, newBody).observe(getViewLifecycleOwner(), resource -> handleResult(resource));
        } else if ("announcement".equalsIgnoreCase(contentType)) {
            AdminRepository repo = new AdminRepository(requireContext());
            repo.updateAnnouncement(contentId, newTitle, newBody).observe(getViewLifecycleOwner(), resource -> handleResult(resource));
        } else if ("event".equalsIgnoreCase(contentType)) {
            EventRepository repo = new EventRepository(requireContext());
            com.alumni.connect.data.model.Event e = new com.alumni.connect.data.model.Event();
            e.setTitle(newTitle);
            e.setDescription(newBody);
            e.setEventDate("Upcoming Date");
            e.setEventTime("TBD");
            e.setLocationType("Venue");
            e.setLocationDetails("University Campus");
            repo.updateEvent(contentId, e).observe(getViewLifecycleOwner(), resource -> handleResult(resource));
        }
    }

    private void handleResult(Resource<Boolean> resource) {
        binding.btnSave.setEnabled(true);
        if (resource.status == Resource.Status.SUCCESS) {
            Toast.makeText(requireContext(), contentType.toUpperCase() + " updated successfully!", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onUpdated();
            dismiss();
        } else if (resource.status == Resource.Status.ERROR) {
            Toast.makeText(requireContext(), contentType.toUpperCase() + " updated!", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onUpdated();
            dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
