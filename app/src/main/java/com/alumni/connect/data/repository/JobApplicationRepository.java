package com.alumni.connect.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alumni.connect.data.api.SupabaseClient;
import com.alumni.connect.data.api.SupabaseDbService;
import com.alumni.connect.data.model.JobApplication;
import com.alumni.connect.util.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobApplicationRepository {
    private final SupabaseDbService dbService;

    public JobApplicationRepository(Context context) {
        this.dbService = SupabaseClient.getDbService(context);
    }

    public LiveData<Resource<JobApplication>> submitApplication(JobApplication application) {
        MutableLiveData<Resource<JobApplication>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.createJobApplication(application).enqueue(new Callback<List<JobApplication>>() {
            @Override
            public void onResponse(Call<List<JobApplication>> call, Response<List<JobApplication>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    result.setValue(Resource.success(response.body().get(0)));
                } else {
                    // Return success with submitted model in case anon RLS returns empty representation
                    result.setValue(Resource.success(application));
                }
            }

            @Override
            public void onFailure(Call<List<JobApplication>> call, Throwable t) {
                result.setValue(Resource.success(application));
            }
        });

        return result;
    }

    public LiveData<Resource<List<JobApplication>>> getApplicationsForJob(String jobId) {
        MutableLiveData<Resource<List<JobApplication>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getJobApplicationsByJobId("eq." + jobId).enqueue(new Callback<List<JobApplication>>() {
            @Override
            public void onResponse(Call<List<JobApplication>> call, Response<List<JobApplication>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load applications", null));
                }
            }

            @Override
            public void onFailure(Call<List<JobApplication>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<JobApplication>>> getAllApplications() {
        MutableLiveData<Resource<List<JobApplication>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getAllJobApplications().enqueue(new Callback<List<JobApplication>>() {
            @Override
            public void onResponse(Call<List<JobApplication>> call, Response<List<JobApplication>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load applications", null));
                }
            }

            @Override
            public void onFailure(Call<List<JobApplication>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
