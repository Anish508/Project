package com.alumni.connect.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alumni.connect.data.api.SupabaseAuthService;
import com.alumni.connect.data.api.SupabaseClient;
import com.alumni.connect.data.api.SupabaseDbService;
import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.data.model.AdminProfile;
import com.alumni.connect.data.model.AlumniProfile;
import com.alumni.connect.data.model.AuthResponse;
import com.alumni.connect.data.model.LoginRequest;
import com.alumni.connect.data.model.SignUpRequest;
import com.alumni.connect.data.model.StudentProfile;
import com.alumni.connect.data.model.User;
import com.alumni.connect.util.Constants;
import com.alumni.connect.util.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final SupabaseAuthService authService;
    private final SupabaseDbService dbService;
    private final SessionManager sessionManager;

    public AuthRepository(Context context) {
        this.authService = SupabaseClient.getAuthService(context);
        this.dbService = SupabaseClient.getDbService(context);
        this.sessionManager = new SessionManager(context);
    }

    public LiveData<Resource<User>> login(String email, String password) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        authService.login(new LoginRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResp = response.body();
                    String accessToken = authResp.getAccessToken();
                    String userId = authResp.getUser() != null ? authResp.getUser().getId() : "";

                    // Fetch user details from public.users table
                    fetchUserProfile(userId, accessToken, result);
                } else {
                    result.setValue(Resource.error("Login failed: " + response.message(), null));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    private void fetchUserProfile(String userId, String accessToken, MutableLiveData<Resource<User>> result) {
        dbService.getUsers("eq." + userId).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    User user = response.body().get(0);
                    sessionManager.saveSession(user.getId(), user.getEmail(), user.getRole(), user.getFullName(), accessToken);
                    result.setValue(Resource.success(user));
                } else {
                    // Fallback user object if DB trigger hasn't fired
                    User user = new User(userId, sessionManager.getEmail(), Constants.ROLE_STUDENT, "Alumni Connect User", "");
                    sessionManager.saveSession(userId, user.getEmail(), user.getRole(), user.getFullName(), accessToken);
                    result.setValue(Resource.success(user));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                User user = new User(userId, "", Constants.ROLE_STUDENT, "Alumni Connect User", "");
                sessionManager.saveSession(userId, "", Constants.ROLE_STUDENT, "Alumni Connect User", accessToken);
                result.setValue(Resource.success(user));
            }
        });
    }

    public LiveData<Resource<User>> register(String email, String password, String fullName, String role,
                                            String department, int year, String company, String designation) {
        MutableLiveData<Resource<User>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        authService.signUp(new SignUpRequest(email, password, fullName, role)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResp = response.body();
                    String userId = authResp.getUser() != null ? authResp.getUser().getId() : "";
                    String token = authResp.getAccessToken();

                    // Create row in public.users table
                    User newUser = new User(userId, email, role, fullName, "");
                    dbService.createUser(newUser).enqueue(new Callback<List<User>>() {
                        @Override
                        public void onResponse(Call<List<User>> call, Response<List<User>> dbResp) {
                            createRoleSpecificProfile(userId, role, department, year, company, designation);
                            sessionManager.saveSession(userId, email, role, fullName, token != null ? token : "");
                            result.setValue(Resource.success(newUser));
                        }

                        @Override
                        public void onFailure(Call<List<User>> call, Throwable t) {
                            createRoleSpecificProfile(userId, role, department, year, company, designation);
                            sessionManager.saveSession(userId, email, role, fullName, token != null ? token : "");
                            result.setValue(Resource.success(newUser));
                        }
                    });
                } else {
                    result.setValue(Resource.error("Registration failed: " + response.message(), null));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    private void createRoleSpecificProfile(String userId, String role, String department, int year, String company, String designation) {
        if (Constants.ROLE_STUDENT.equals(role)) {
            StudentProfile profile = new StudentProfile();
            profile.setUserId(userId);
            profile.setDepartment(department);
            profile.setBatchYear(year);
            dbService.createStudentProfile(profile).enqueue(new Callback<List<StudentProfile>>() {
                @Override public void onResponse(Call<List<StudentProfile>> c, Response<List<StudentProfile>> r) {}
                @Override public void onFailure(Call<List<StudentProfile>> c, Throwable t) {}
            });
        } else if (Constants.ROLE_ALUMNI.equals(role)) {
            AlumniProfile profile = new AlumniProfile();
            profile.setUserId(userId);
            profile.setDepartment(department);
            profile.setGraduationYear(year);
            profile.setCurrentCompany(company);
            profile.setDesignation(designation);
            dbService.createAlumniProfile(profile).enqueue(new Callback<List<AlumniProfile>>() {
                @Override public void onResponse(Call<List<AlumniProfile>> c, Response<List<AlumniProfile>> r) {}
                @Override public void onFailure(Call<List<AlumniProfile>> c, Throwable t) {}
            });
        } else if (Constants.ROLE_ADMIN.equals(role)) {
            AdminProfile profile = new AdminProfile();
            profile.setUserId(userId);
            profile.setDepartment(department);
            dbService.createAdminProfile(profile).enqueue(new Callback<List<AdminProfile>>() {
                @Override public void onResponse(Call<List<AdminProfile>> c, Response<List<AdminProfile>> r) {}
                @Override public void onFailure(Call<List<AdminProfile>> c, Throwable t) {}
            });
        }
    }

    public LiveData<Resource<String>> resetPassword(String email) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        authService.resetPassword(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success("Password reset email sent!"));
                } else {
                    result.setValue(Resource.error("Failed to send reset email.", null));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public void logout() {
        sessionManager.clearSession();
    }
}
