package com.alumni.connect.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alumni.connect.data.api.SupabaseClient;
import com.alumni.connect.data.api.SupabaseDbService;
import com.alumni.connect.data.model.AlumniProfile;
import com.alumni.connect.data.model.User;
import com.alumni.connect.util.Resource;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {
    private final SupabaseDbService dbService;

    public ProfileRepository(Context context) {
        this.dbService = SupabaseClient.getDbService(context);
    }

    public LiveData<Resource<List<AlumniProfile>>> getAllAlumniProfiles() {
        MutableLiveData<Resource<List<AlumniProfile>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getAllAlumniProfiles().enqueue(new Callback<List<AlumniProfile>>() {
            @Override
            public void onResponse(Call<List<AlumniProfile>> call, Response<List<AlumniProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load alumni profiles.", null));
                }
            }

            @Override
            public void onFailure(Call<List<AlumniProfile>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<List<User>>> getAllUsers() {
        MutableLiveData<Resource<List<User>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load users.", null));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<User>> updateUserProfile(String userId, Map<String, Object> updates) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.updateUser("eq." + userId, updates).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    result.setValue(Resource.success(response.body().get(0)));
                } else {
                    result.setValue(Resource.error("Failed to update profile.", null));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
