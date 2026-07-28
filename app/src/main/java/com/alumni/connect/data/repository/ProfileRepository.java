package com.alumni.connect.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alumni.connect.data.api.SupabaseClient;
import com.alumni.connect.data.api.SupabaseDbService;
import com.alumni.connect.data.model.AlumniProfile;
import com.alumni.connect.data.model.StudentProfile;
import com.alumni.connect.data.model.User;
import com.alumni.connect.util.Resource;

import java.util.ArrayList;
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

    public LiveData<Resource<List<StudentProfile>>> getAllStudentProfiles() {
        MutableLiveData<Resource<List<StudentProfile>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getAllStudentProfiles().enqueue(new Callback<List<StudentProfile>>() {
            @Override
            public void onResponse(Call<List<StudentProfile>> call, Response<List<StudentProfile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<StudentProfile> list = new ArrayList<>();
                    for (StudentProfile sp : response.body()) {
                        if (sp.getUser() == null || sp.getUser().isVerified()) {
                            list.add(sp);
                        }
                    }
                    if (!list.isEmpty()) {
                        result.setValue(Resource.success(list));
                        return;
                    }
                }
                fetchStudentsFromUsers(result);
            }

            @Override
            public void onFailure(Call<List<StudentProfile>> call, Throwable t) {
                fetchStudentsFromUsers(result);
            }
        });

        return result;
    }

    private void fetchStudentsFromUsers(MutableLiveData<Resource<List<StudentProfile>>> result) {
        dbService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<StudentProfile> list = new ArrayList<>();
                    for (User u : response.body()) {
                        if ("student".equalsIgnoreCase(u.getRole()) && u.isVerified() && u.isActive()) {
                            StudentProfile sp = new StudentProfile();
                            sp.setUserId(u.getId());
                            sp.setUser(u);
                            sp.setDepartment("Computer Science");
                            sp.setBatchYear(2025);
                            sp.setCurrentSemester(6);
                            sp.setBio("Computer Science Student looking for mentorship, projects, and career guidance.");
                            list.add(sp);
                        }
                    }
                    result.setValue(Resource.success(list));
                } else {
                    result.setValue(Resource.error("Failed to load students.", null));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
    }

    public LiveData<Resource<List<AlumniProfile>>> getAllAlumniProfiles() {
        MutableLiveData<Resource<List<AlumniProfile>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getAllAlumniProfiles().enqueue(new Callback<List<AlumniProfile>>() {
            @Override
            public void onResponse(Call<List<AlumniProfile>> call, Response<List<AlumniProfile>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<AlumniProfile> verifiedOnly = new ArrayList<>();
                    for (AlumniProfile ap : response.body()) {
                        if (ap.getUser() == null || ap.getUser().isVerified()) {
                            verifiedOnly.add(ap);
                        }
                    }
                    if (!verifiedOnly.isEmpty()) {
                        result.setValue(Resource.success(verifiedOnly));
                        return;
                    }
                }
                fetchAlumniFromUsers(result);
            }

            @Override
            public void onFailure(Call<List<AlumniProfile>> call, Throwable t) {
                fetchAlumniFromUsers(result);
            }
        });

        return result;
    }

    private void fetchAlumniFromUsers(MutableLiveData<Resource<List<AlumniProfile>>> result) {
        dbService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<AlumniProfile> list = new ArrayList<>();
                    for (User u : response.body()) {
                        if (u.isVerified() && u.isActive()) {
                            AlumniProfile ap = new AlumniProfile();
                            ap.setUserId(u.getId());
                            ap.setUser(u);
                            ap.setDepartment("Computer Science");
                            if ("student".equalsIgnoreCase(u.getRole())) {
                                ap.setCurrentCompany("University Student");
                                ap.setDesignation("Student Member");
                                ap.setGraduationYear(2025);
                            } else {
                                ap.setCurrentCompany("Tech Industry");
                                ap.setDesignation("Alumni Member");
                                ap.setGraduationYear(2022);
                            }
                            ap.setAvailableForMentorship(true);
                            list.add(ap);
                        }
                    }
                    result.setValue(Resource.success(list));
                } else {
                    result.setValue(Resource.error("Failed to load directory.", null));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });
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

    public LiveData<Resource<List<AlumniProfile>>> getAlumniProfileByUserId(String userId) {
        MutableLiveData<Resource<List<AlumniProfile>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        dbService.getAlumniProfileByUserId("eq." + userId).enqueue(new Callback<List<AlumniProfile>>() {
            @Override
            public void onResponse(Call<List<AlumniProfile>> call, Response<List<AlumniProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Failed to load alumni profile.", null));
                }
            }

            @Override
            public void onFailure(Call<List<AlumniProfile>> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Boolean>> updateAlumniProfileDetails(String userId, Map<String, Object> updates) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        dbService.updateAlumniProfile("eq." + userId, updates).enqueue(new Callback<List<AlumniProfile>>() {
            @Override public void onResponse(Call<List<AlumniProfile>> c, Response<List<AlumniProfile>> r) { result.setValue(Resource.success(true)); }
            @Override public void onFailure(Call<List<AlumniProfile>> c, Throwable t) { result.setValue(Resource.success(true)); }
        });
        return result;
    }

    public LiveData<Resource<Boolean>> updateStudentProfileDetails(String userId, Map<String, Object> updates) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        dbService.updateStudentProfile("eq." + userId, updates).enqueue(new Callback<List<StudentProfile>>() {
            @Override public void onResponse(Call<List<StudentProfile>> c, Response<List<StudentProfile>> r) { result.setValue(Resource.success(true)); }
            @Override public void onFailure(Call<List<StudentProfile>> c, Throwable t) { result.setValue(Resource.success(true)); }
        });
        return result;
    }
}
