package com.alumni.connect.data.api;

import android.content.Context;

import com.alumni.connect.data.local.SessionManager;
import com.alumni.connect.util.SupabaseConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    private final Context context;

    public HeaderInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String apiKey = SupabaseConfig.getSupabaseKey(context);
        if (apiKey == null) apiKey = "";
        
        SessionManager sessionManager = new SessionManager(context);
        String authToken = sessionManager.getAccessToken();

        Request.Builder builder = original.newBuilder()
                .header("apikey", apiKey)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation");

        if (authToken != null && !authToken.trim().isEmpty()) {
            builder.header("Authorization", "Bearer " + authToken.trim());
        } else {
            builder.header("Authorization", "Bearer " + apiKey);
        }

        return chain.proceed(builder.build());
    }
}
