package com.alumni.connect.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alumni.connect.data.api.SupabaseClient;
import com.alumni.connect.data.api.SupabaseDbService;
import com.alumni.connect.data.model.Job;
import com.alumni.connect.util.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobRepository {
    private final SupabaseDbService dbService;

    public JobRepository(Context context) {
        this.dbService = SupabaseClient.getDbService(context);
    }

    public LiveData<Resource<List<Job>>> getJobs() {
        MutableLiveData<Resource<List<Job>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getJobs().enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to fetch jobs.", null));
                }
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Job>> createJob(Job job) {
        MutableLiveData<Resource<Job>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.createJob(job).enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    result.setValue(Resource.success(response.body().get(0)));
                } else {
                    result.setValue(Resource.error("Failed to post job.", null));
                }
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
