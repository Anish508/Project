package com.alumni.connect.ui.jobs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alumni.connect.data.model.Job;
import com.alumni.connect.data.repository.JobRepository;
import com.alumni.connect.util.Resource;

import java.util.List;

public class JobsViewModel extends AndroidViewModel {
    private final JobRepository jobRepository;

    public JobsViewModel(@NonNull Application application) {
        super(application);
        this.jobRepository = new JobRepository(application);
    }

    public LiveData<Resource<List<Job>>> getJobs() {
        return jobRepository.getJobs();
    }

    public LiveData<Resource<Job>> createJob(Job job) {
        return jobRepository.createJob(job);
    }
}
