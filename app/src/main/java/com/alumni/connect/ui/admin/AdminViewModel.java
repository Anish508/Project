package com.alumni.connect.ui.admin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alumni.connect.data.model.Post;
import com.alumni.connect.data.repository.PostRepository;
import com.alumni.connect.util.Resource;

public class AdminViewModel extends AndroidViewModel {
    private final PostRepository postRepository;

    public AdminViewModel(@NonNull Application application) {
        super(application);
        this.postRepository = new PostRepository(application);
    }

    public LiveData<Resource<Post>> publishAnnouncement(Post post) {
        return postRepository.createPost(post);
    }
}
