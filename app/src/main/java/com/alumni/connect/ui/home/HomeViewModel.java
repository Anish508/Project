package com.alumni.connect.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alumni.connect.data.model.Post;
import com.alumni.connect.data.repository.PostRepository;
import com.alumni.connect.util.Resource;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final PostRepository postRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.postRepository = new PostRepository(application);
    }

    public LiveData<Resource<List<Post>>> getPosts() {
        return postRepository.getPosts();
    }
}
