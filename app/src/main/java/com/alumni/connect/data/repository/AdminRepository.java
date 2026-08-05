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
                if (response.isSuccessful()) {
                    result.setValue(Resource.success("Updated successfully"));
                } else {
                    result.setValue(Resource.error("Update failed: HTTP " + response.code(), null));
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                result.setValue(Resource.error("Update failed: " + t.getMessage(), null));
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
                if (response.isSuccessful()) {
                    result.setValue(Resource.success("User deleted"));
                } else {
                    result.setValue(Resource.error("Delete failed: HTTP " + response.code(), null));
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Delete failed: " + t.getMessage(), null));
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
                    List<Post> publicPosts = new java.util.ArrayList<>();
                    for (Post p : response.body()) {
                        if (p.getPostType() == null || !p.getPostType().toLowerCase().startsWith("chat")) {
                            publicPosts.add(p);
                        }
                    }
                    result.setValue(Resource.success(publicPosts));
                } else {
                    result.setValue(Resource.error("Failed to load posts", null));
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
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
                if (response.isSuccessful()) {
                    result.setValue(Resource.success("Post deleted"));
                } else {
                    result.setValue(Resource.error("Delete failed: HTTP " + response.code(), null));
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Delete failed: " + t.getMessage(), null));
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
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
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
                if (response.isSuccessful()) {
                    result.setValue(Resource.success("Job deleted"));
                } else {
                    result.setValue(Resource.error("Delete failed: HTTP " + response.code(), null));
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Delete failed: " + t.getMessage(), null));
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
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
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
                if (response.isSuccessful()) {
                    result.setValue(Resource.success("Event deleted"));
                } else {
                    result.setValue(Resource.error("Delete failed: HTTP " + response.code(), null));
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Delete failed: " + t.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<Event>> createEvent(Event event) {
        MutableLiveData<Resource<Event>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        if (event.getCreatedBy() != null && event.getCreatedBy().trim().isEmpty()) {
            event.setCreatedBy(null);
        }

        dbService.createEvent(event).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(event));
                } else {
                    result.setValue(Resource.error("Failed to create event: HTTP " + response.code(), null));
                }
            }
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                result.setValue(Resource.error("Failed to create event: " + t.getMessage(), null));
            }
        });
        return result;
    }

    // ==================== ANNOUNCEMENTS ====================

    public LiveData<Resource<Announcement>> createAnnouncement(Announcement announcement) {
        MutableLiveData<Resource<Announcement>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        String adminId = announcement.getAdminId();
        if (adminId != null && adminId.trim().isEmpty()) {
            announcement.setAdminId(null);
        }

        dbService.createAnnouncement(announcement).enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {
                if (response.isSuccessful()) {
                    // Sync into posts table so announcement appears across home feeds & moderation
                    Post post = new Post();
                    post.setTitle(announcement.getTitle());
                    post.setContent(announcement.getMessage());
                    post.setPostType("announcement");
                    post.setAuthorId(announcement.getAdminId());
                    
                    dbService.createPost(post).enqueue(new Callback<List<Post>>() {
                        @Override
                        public void onResponse(Call<List<Post>> c, Response<List<Post>> r) {
                            result.setValue(Resource.success(announcement));
                        }
                        @Override
                        public void onFailure(Call<List<Post>> c, Throwable t) {
                            result.setValue(Resource.success(announcement));
                        }
                    });
                } else {
                    result.setValue(Resource.error("Failed to broadcast announcement: HTTP " + response.code(), null));
                }
            }
            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {
                result.setValue(Resource.error("Failed to broadcast announcement: " + t.getMessage(), null));
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
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
        return result;
    }

    public LiveData<Resource<Boolean>> updateAnnouncement(String id, String title, String message) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("message", message);

        dbService.updateAnnouncement("eq." + id, updates).enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(true));
                } else {
                    result.setValue(Resource.error("Failed to update announcement", false));
                }
            }

            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), false));
            }
        });

        return result;
    }

    // ==================== EVENT REGISTRATIONS (ADMIN) ====================

    public LiveData<Resource<Integer>> getTotalRsvpCount() {
        MutableLiveData<Resource<Integer>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getAllEventRegistrations().enqueue(new Callback<List<com.alumni.connect.data.model.EventRegistration>>() {
            @Override
            public void onResponse(Call<List<com.alumni.connect.data.model.EventRegistration>> call,
                                   Response<List<com.alumni.connect.data.model.EventRegistration>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body().size()));
                } else {
                    result.setValue(Resource.error("Failed to load RSVP count", 0));
                }
            }

            @Override
            public void onFailure(Call<List<com.alumni.connect.data.model.EventRegistration>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), 0));
            }
        });

        return result;
    }

    // ==================== JOB APPLICATIONS (ADMIN) ====================

    public LiveData<Resource<Integer>> getTotalApplicationsCount() {
        MutableLiveData<Resource<Integer>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getAllJobApplications().enqueue(new Callback<List<com.alumni.connect.data.model.JobApplication>>() {
            @Override
            public void onResponse(Call<List<com.alumni.connect.data.model.JobApplication>> call,
                                   Response<List<com.alumni.connect.data.model.JobApplication>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body().size()));
                } else {
                    result.setValue(Resource.error("Failed to load application count", 0));
                }
            }

            @Override
            public void onFailure(Call<List<com.alumni.connect.data.model.JobApplication>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), 0));
            }
        });

        return result;
    }

    // ==================== MENTORSHIP (ADMIN) ====================

    public LiveData<Resource<Integer>> getTotalMentorshipCount() {
        MutableLiveData<Resource<Integer>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getAllMentorshipRequests().enqueue(new Callback<List<com.alumni.connect.data.model.MentorshipRequest>>() {
            @Override
            public void onResponse(Call<List<com.alumni.connect.data.model.MentorshipRequest>> call,
                                   Response<List<com.alumni.connect.data.model.MentorshipRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body().size()));
                } else {
                    result.setValue(Resource.error("Failed to load mentorship count", 0));
                }
            }

            @Override
            public void onFailure(Call<List<com.alumni.connect.data.model.MentorshipRequest>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), 0));
            }
        });

        return result;
    }
}


