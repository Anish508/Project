package com.alumni.connect.data.api;

import android.content.Context;

import com.alumni.connect.util.SupabaseConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {
    private static Retrofit retrofit = null;
    private static String currentBaseUrl = "";

    public static Retrofit getClient(Context context) {
        String baseUrl = SupabaseConfig.getSupabaseUrl(context);
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        if (retrofit == null || !currentBaseUrl.equals(baseUrl)) {
            currentBaseUrl = baseUrl;

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new HeaderInterceptor(context))
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static SupabaseAuthService getAuthService(Context context) {
        return getClient(context).create(SupabaseAuthService.class);
    }

    public static SupabaseDbService getDbService(Context context) {
        return getClient(context).create(SupabaseDbService.class);
    }
}
