package com.alumni.connect.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alumni.connect.data.api.SupabaseClient;
import com.alumni.connect.data.api.SupabaseDbService;
import com.alumni.connect.data.model.Event;
import com.alumni.connect.util.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventRepository {
    private final SupabaseDbService dbService;

    public EventRepository(Context context) {
        this.dbService = SupabaseClient.getDbService(context);
    }

    public LiveData<Resource<List<Event>>> getEvents() {
        MutableLiveData<Resource<List<Event>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load events.", null));
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Event>> createEvent(Event event) {
        MutableLiveData<Resource<Event>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.createEvent(event).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    result.setValue(Resource.success(response.body().get(0)));
                } else {
                    result.setValue(Resource.error("Failed to create event.", null));
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
