package com.alumni.connect.ui.mentorship;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.data.model.Post;
import com.alumni.connect.data.repository.PostRepository;
import com.alumni.connect.databinding.FragmentChatBinding;
import com.alumni.connect.util.Resource;

import java.util.List;

/**
 * ChatFragment — lightweight in-app 1-on-1 chat for accepted mentorship sessions.
 *
 * Messages are stored in the `posts` table with:
 *   post_type = "chat_<mentorshipRequestId>"
 *   author_id = sender user_id
 *   title     = sender display name
 *   content   = message text
 *
 * Arguments (via Bundle):
 *   "request_id"   — the mentorship_request UUID
 *   "request_topic"— the topic of the mentorship (for display)
 *   "other_name"   — the other participant's display name
 */
public class ChatFragment extends Fragment {

    public static final String ARG_REQUEST_ID = "request_id";
    public static final String ARG_TOPIC = "request_topic";
    public static final String ARG_OTHER_NAME = "other_name";

    private FragmentChatBinding binding;
    private PostRepository postRepository;
    private SessionManager sessionManager;
    private ChatMessageAdapter adapter;

    private String requestId;
    private String topic;
    private String otherName;

    public static ChatFragment newInstance(String requestId, String topic, String otherName) {
        ChatFragment f = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REQUEST_ID, requestId);
        args.putString(ARG_TOPIC, topic);
        args.putString(ARG_OTHER_NAME, otherName != null ? otherName : "Mentor/Mentee");
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        postRepository = new PostRepository(requireContext());

        // Read arguments
        if (getArguments() != null) {
            requestId = getArguments().getString(ARG_REQUEST_ID, "");
            topic = getArguments().getString(ARG_TOPIC, "Mentorship Chat");
            otherName = getArguments().getString(ARG_OTHER_NAME, "Mentor/Mentee");
        }

        // Setup header
        binding.tvChatTitle.setText(otherName);
        binding.tvChatSubtitle.setText("Topic: " + topic);

        // Setup RecyclerView
        adapter = new ChatMessageAdapter(sessionManager.getUserId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true); // newest messages at bottom
        binding.rvChatMessages.setLayoutManager(layoutManager);
        binding.rvChatMessages.setAdapter(adapter);

        // Back button
        binding.btnChatBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else {
                try {
                    Navigation.findNavController(requireView()).navigateUp();
                } catch (Exception ignored) {}
            }
        });

        // Send button
        binding.btnSendMessage.setOnClickListener(v -> sendMessage());

        // Also send on keyboard "Send" action
        binding.etChatMessage.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        loadMessages();
    }

    private void loadMessages() {
        if (requestId == null || requestId.isEmpty()) {
            binding.tvChatEmpty.setVisibility(View.VISIBLE);
            binding.rvChatMessages.setVisibility(View.GONE);
            return;
        }

        binding.pbChatLoading.setVisibility(View.VISIBLE);
        binding.tvChatEmpty.setVisibility(View.GONE);
        binding.rvChatMessages.setVisibility(View.GONE);

        postRepository.getChatMessages(requestId).observe(getViewLifecycleOwner(), resource -> {
            binding.pbChatLoading.setVisibility(View.GONE);

            if (resource.status == Resource.Status.SUCCESS) {
                List<Post> messages = resource.data;
                if (messages != null && !messages.isEmpty()) {
                    binding.rvChatMessages.setVisibility(View.VISIBLE);
                    binding.tvChatEmpty.setVisibility(View.GONE);
                    adapter.setMessages(messages);
                    // Scroll to latest
                    binding.rvChatMessages.scrollToPosition(messages.size() - 1);
                } else {
                    binding.rvChatMessages.setVisibility(View.GONE);
                    binding.tvChatEmpty.setVisibility(View.VISIBLE);
                }
            } else if (resource.status == Resource.Status.ERROR) {
                binding.tvChatEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void sendMessage() {
        String text = binding.etChatMessage.getText() != null
                ? binding.etChatMessage.getText().toString().trim()
                : "";

        if (text.isEmpty()) return;

        String senderId = sessionManager.getUserId();
        String senderName = sessionManager.getFullName();
        if (senderName == null || senderName.isEmpty()) senderName = sessionManager.getEmail();

        // Optimistic UI: add message immediately
        Post optimisticMsg = new Post();
        optimisticMsg.setAuthorId(senderId);
        optimisticMsg.setTitle(senderName);
        optimisticMsg.setContent(text);
        optimisticMsg.setPostType("chat_" + requestId);
        adapter.addMessage(optimisticMsg);
        binding.rvChatMessages.setVisibility(View.VISIBLE);
        binding.tvChatEmpty.setVisibility(View.GONE);
        binding.rvChatMessages.scrollToPosition(adapter.getItemCount() - 1);

        // Clear input
        binding.etChatMessage.setText("");

        // Send to Supabase
        final String finalSenderName = senderName;
        postRepository.sendChatMessage(requestId, senderId, finalSenderName, text)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (resource.status == Resource.Status.ERROR) {
                        Toast.makeText(requireContext(), "Failed to send: " + resource.message, Toast.LENGTH_SHORT).show();
                    }
                    // On success, the message is already shown via optimistic update
                    // Optionally reload to sync created_at timestamps
                    if (resource.status == Resource.Status.SUCCESS) {
                        loadMessages();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
