package com.alumni.connect.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alumni.connect.data.api.SupabaseClient;
import com.alumni.connect.data.api.SupabaseDbService;
import com.alumni.connect.data.model.MentorshipRequest;
import com.alumni.connect.util.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MentorshipRepository {
    private final SupabaseDbService dbService;

    public MentorshipRepository(Context context) {
        this.dbService = SupabaseClient.getDbService(context);
    }

    public LiveData<Resource<List<MentorshipRequest>>> getRequestsForUser(String userId) {
        MutableLiveData<Resource<List<MentorshipRequest>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getMentorshipRequestsForUser("eq." + userId).enqueue(new Callback<List<MentorshipRequest>>() {
            @Override
            public void onResponse(Call<List<MentorshipRequest>> call, Response<List<MentorshipRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load mentorship requests.", null));
                }
            }

            @Override
            public void onFailure(Call<List<MentorshipRequest>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<MentorshipRequest>> createMentorshipRequest(MentorshipRequest request) {
        MutableLiveData<Resource<MentorshipRequest>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.createMentorshipRequest(request).enqueue(new Callback<List<MentorshipRequest>>() {
            @Override
            public void onResponse(Call<List<MentorshipRequest>> call, Response<List<MentorshipRequest>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    result.setValue(Resource.success(response.body().get(0)));
                } else {
                    result.setValue(Resource.error("Failed to send mentorship request.", null));
                }
            }

            @Override
            public void onFailure(Call<List<MentorshipRequest>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> updateStatus(String requestId, String status) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, Object> update = new HashMap<>();
        update.put("status", status);

        dbService.updateMentorshipStatus("eq." + requestId, update).enqueue(new Callback<List<MentorshipRequest>>() {
            @Override
            public void onResponse(Call<List<MentorshipRequest>> call, Response<List<MentorshipRequest>> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Failed to update status.", false));
                }
            }

            @Override
            public void onFailure(Call<List<MentorshipRequest>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), false));
            }
        });

        return result;
    }
}
