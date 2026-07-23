package com.alumni.connect.data.api;

import com.alumni.connect.data.model.AuthResponse;
import com.alumni.connect.data.model.LoginRequest;
import com.alumni.connect.data.model.SignUpRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SupabaseAuthService {

    @POST("auth/v1/signup")
    Call<AuthResponse> signUp(@Body SignUpRequest request);

    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/v1/recover")
    Call<Void> resetPassword(@Body Map<String, String> body);

    @POST("auth/v1/logout")
    Call<Void> logout();
}
