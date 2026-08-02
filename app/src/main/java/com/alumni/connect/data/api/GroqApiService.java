package com.alumni.connect.data.api;

import com.alumni.connect.data.model.GroqRequest;
import com.alumni.connect.data.model.GroqResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GroqApiService {
    @Headers("Content-Type: application/json")
    @POST("openai/v1/chat/completions")
    Call<GroqResponse> generateChatCompletion(
        @Header("Authorization") String authHeader,
        @Body GroqRequest request
    );
}
