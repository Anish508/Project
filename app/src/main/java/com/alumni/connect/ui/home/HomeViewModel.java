package com.alumni.connect.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alumni.connect.data.model.AlumniProfile;
import com.alumni.connect.data.model.Job;
import com.alumni.connect.data.model.Post;
import com.alumni.connect.data.repository.JobRepository;
import com.alumni.connect.data.repository.PostRepository;
import com.alumni.connect.data.repository.ProfileRepository;
import com.alumni.connect.util.Resource;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final JobRepository jobRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.postRepository = new PostRepository(application);
        this.profileRepository = new ProfileRepository(application);
        this.jobRepository = new JobRepository(application);
    }

    public LiveData<Resource<List<Post>>> getPosts() {
        return postRepository.getPosts();
    }

    public LiveData<Resource<List<AlumniProfile>>> getAlumniProfiles() {
        return profileRepository.getAllAlumniProfiles();
    }

    public LiveData<Resource<List<Job>>> getJobs() {
        return jobRepository.getJobs();
    }
}
