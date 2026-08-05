package com.alumni.connect.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alumni.connect.data.api.SupabaseClient;
import com.alumni.connect.data.api.SupabaseDbService;
import com.alumni.connect.data.model.Post;
import com.alumni.connect.util.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRepository {
    private final SupabaseDbService dbService;

    public PostRepository(Context context) {
        this.dbService = SupabaseClient.getDbService(context);
    }

    public LiveData<Resource<List<Post>>> getPosts() {
        MutableLiveData<Resource<List<Post>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> publicPosts = new java.util.ArrayList<>();
                    for (Post p : response.body()) {
                        if (p.getPostType() == null || !p.getPostType().toLowerCase().startsWith("chat")) {
                            publicPosts.add(p);
                        }
                    }
                    result.setValue(Resource.success(publicPosts));
                } else {
                    result.setValue(Resource.error("Failed to load community posts.", null));
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Post>> createPost(Post post) {
        MutableLiveData<Resource<Post>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        if (post.getAuthorId() != null && post.getAuthorId().trim().isEmpty()) {
            post.setAuthorId(null);
        }

        dbService.createPost(post).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) {
                        result.setValue(Resource.success(response.body().get(0)));
                    } else {
                        result.setValue(Resource.success(post));
                    }
                } else {
                    result.setValue(Resource.error("Failed to publish post: HTTP " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> updatePost(String id, String title, String content) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("title", title);
        updates.put("content", content);

        dbService.updatePost("eq." + id, updates).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Failed to update post.", false));
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), false));
            }
        });

        return result;
    }

    // ==================== CHAT (uses posts table with post_type = "chat_<requestId>") ====================

    /**
     * Fetch all chat messages for a mentorship session.
     * @param requestId the mentorship_request id
     */
    public LiveData<Resource<List<Post>>> getChatMessages(String requestId) {
        MutableLiveData<Resource<List<Post>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        String chatType = "eq.chat_" + requestId;

        dbService.getChatMessages(chatType).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load messages", null));
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    /**
     * Send a chat message in a mentorship session.
     */
    public LiveData<Resource<Post>> sendChatMessage(String requestId, String senderId, String senderName, String messageText) {
        Post msg = new Post();
        msg.setPostType("chat_" + requestId);
        msg.setAuthorId(senderId);
        msg.setTitle(senderName);  // title stores sender's display name
        msg.setContent(messageText);
        return createPost(msg);
    }
}

