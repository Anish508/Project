package com.alumni.connect.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alumni.connect.data.model.Post;
import com.alumni.connect.databinding.DialogPostDetailBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PostDetailDialogFragment extends BottomSheetDialogFragment {
    private DialogPostDetailBinding binding;
    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTENT = "content";
    private static final String ARG_TYPE = "type";
    private static final String ARG_DATE = "date";

    public static PostDetailDialogFragment newInstance(Post post) {
        PostDetailDialogFragment fragment = new PostDetailDialogFragment();
        Bundle args = new Bundle();
        if (post != null) {
            args.putString(ARG_TITLE, post.getTitle());
            args.putString(ARG_CONTENT, post.getContent());
            args.putString(ARG_TYPE, post.getPostType());
            args.putString(ARG_DATE, post.getCreatedAt());
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogPostDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString(ARG_TITLE, "Untitled Post");
            String content = args.getString(ARG_CONTENT, "No content available.");
            String type = args.getString(ARG_TYPE, "POST");
            String date = args.getString(ARG_DATE, "");

            binding.tvPostTitle.setText(title);
            binding.tvPostContent.setText(content);
            binding.tvPostTypeBadge.setText(type != null ? type.toUpperCase() : "POST");

            if (date != null && !date.isEmpty()) {
                binding.tvPostAuthorDate.setText("Published • " + date.substring(0, Math.min(10, date.length())));
            } else {
                binding.tvPostAuthorDate.setText("Published by Alumni Community");
            }
        }

        binding.btnClose.setOnClickListener(v -> dismiss());
        binding.btnCloseButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
