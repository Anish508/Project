package com.alumni.connect.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alumni.connect.data.api.SupabaseClient;
import com.alumni.connect.data.api.SupabaseDbService;
import com.alumni.connect.data.model.Announcement;
import com.alumni.connect.data.model.Event;
import com.alumni.connect.data.model.Job;
import com.alumni.connect.data.model.Post;
import com.alumni.connect.data.model.User;
import com.alumni.connect.util.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRepository {
    private final SupabaseDbService dbService;

    public AdminRepository(Context context) {
        this.dbService = SupabaseClient.getDbService(context);
    }

    // ==================== USER MANAGEMENT ====================

    public LiveData<Resource<List<User>>> getAllUsers() {
        MutableLiveData<Resource<List<User>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        dbService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load users", null));
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<User>>> getUsersByRole(String role) {
        MutableLiveData<Resource<List<User>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        dbService.getUsersByRole("eq." + role).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load users", null));
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<String>> updateUserField(String userId, String field, Object value) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        Map<String, Object> updates = new HashMap<>();
        updates.put(field, value);
        dbService.updateUser("eq." + userId, updates).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                result.setValue(Resource.success("Updated successfully"));
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                result.setValue(Resource.error("Update failed", null));
            }
        });
        return result;
    }

    public LiveData<Resource<String>> deleteUser(String userId) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        dbService.deleteUser("eq." + userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.setValue(Resource.success("User deleted"));
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Delete failed", null));
            }
        });
        return result;
    }

    public LiveData<Resource<String>> verifyUser(String userId) {
        return updateUserField(userId, "is_verified", true);
    }

    public LiveData<Resource<String>> suspendUser(String userId) {
        return updateUserField(userId, "is_active", false);
    }

    public LiveData<Resource<String>> activateUser(String userId) {
        return updateUserField(userId, "is_active", true);
    }

    public LiveData<Resource<String>> changeUserRole(String userId, String newRole) {
        return updateUserField(userId, "role", newRole);
    }

    // ==================== CONTENT MODERATION ====================

    public LiveData<Resource<List<Post>>> getAllPosts() {
        MutableLiveData<Resource<List<Post>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        dbService.getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load posts", null));
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                result.setValue(Resource.error("Network error", null));
            }
        });
        return result;
    }

    public LiveData<Resource<String>> deletePost(String postId) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        dbService.deletePost("eq." + postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.setValue(Resource.success("Post deleted"));
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Delete failed", null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<Job>>> getAllJobs() {
        MutableLiveData<Resource<List<Job>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        dbService.getJobs().enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load jobs", null));
                }
            }
            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                result.setValue(Resource.error("Network error", null));
            }
        });
        return result;
    }

    public LiveData<Resource<String>> deleteJob(String jobId) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        dbService.deleteJob("eq." + jobId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.setValue(Resource.success("Job deleted"));
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Delete failed", null));
            }
        });
        return result;
    }

    // ==================== EVENT MANAGEMENT ====================

    public LiveData<Resource<List<Event>>> getAllEvents() {
        MutableLiveData<Resource<List<Event>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        dbService.getEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load events", null));
                }
            }
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                result.setValue(Resource.error("Network error", null));
            }
        });
        return result;
    }

    public LiveData<Resource<String>> deleteEvent(String eventId) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        dbService.deleteEvent("eq." + eventId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.setValue(Resource.success("Event deleted"));
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Delete failed", null));
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
                result.setValue(Resource.success(event));
            }
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                result.setValue(Resource.error("Failed to create event", null));
            }
        });
        return result;
    }

    // ==================== ANNOUNCEMENTS ====================

    public LiveData<Resource<Announcement>> createAnnouncement(Announcement announcement) {
        MutableLiveData<Resource<Announcement>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        dbService.createAnnouncement(announcement).enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {
                result.setValue(Resource.success(announcement));
            }
            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {
                result.setValue(Resource.error("Failed to create announcement", null));
            }
        });
        return result;
    }

    public LiveData<Resource<List<Announcement>>> getAnnouncements() {
        MutableLiveData<Resource<List<Announcement>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        dbService.getAnnouncements().enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load announcements", null));
                }
            }
            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {
                result.setValue(Resource.error("Network error", null));
            }
        });
        return result;
    }
}
