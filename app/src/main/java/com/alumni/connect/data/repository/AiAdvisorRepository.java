package com.alumni.connect.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alumni.connect.data.api.GroqApiClient;
import com.alumni.connect.data.api.GroqApiService;
import com.alumni.connect.data.model.GroqMessage;
import com.alumni.connect.data.model.GroqRequest;
import com.alumni.connect.data.model.GroqResponse;
import com.alumni.connect.util.Resource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiAdvisorRepository {
    private final GroqApiService apiService;

    public AiAdvisorRepository() {
        this.apiService = GroqApiClient.getApiService();
    }

    public LiveData<Resource<String>> evaluateResumeMatch(String jobTitle, String jobDescription, String studentSkills) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        List<GroqMessage> messages = new ArrayList<>();
        messages.add(new GroqMessage("system", 
            "You are an expert AI Career Advisor & Technical Recruiter for university alumni networks. " +
            "Analyze the candidate's skills against the job posting. " +
            "Return a clean, well-formatted Markdown evaluation that includes:\n" +
            "1. **Match Score**: Provide an estimated match percentage (0-100%).\n" +
            "2. **Strongest Qualifications**: Highlight matched skills.\n" +
            "3. **Skill Gaps & Recommendations**: List missing requirements.\n" +
            "4. **3 Action Tips**: Specific ways to tailor the resume for this role."
        ));

        String prompt = "Job Title: " + jobTitle + "\n" +
                        "Job Description: " + jobDescription + "\n" +
                        "Candidate Skills / Experience: " + (studentSkills.isEmpty() ? "Standard Computer Science & Software Development Background" : studentSkills);

        messages.add(new GroqMessage("user", prompt));

        GroqRequest request = new GroqRequest("llama-3.3-70b-versatile", messages);
        request.setTemperature(0.4);
        request.setMaxTokens(1024);

        String authHeader = "Bearer " + GroqApiClient.GROQ_API_KEY;

        apiService.generateChatCompletion(authHeader, request).enqueue(new Callback<GroqResponse>() {
            @Override
            public void onResponse(Call<GroqResponse> call, Response<GroqResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String content = response.body().getFirstChoiceContent();
                    if (!content.isEmpty()) {
                        result.setValue(Resource.success(content));
                    } else {
                        result.setValue(Resource.error("AI returned an empty response.", null));
                    }
                } else {
                    result.setValue(Resource.error("Groq AI Service Error: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<GroqResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error connecting to Groq AI: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<String>> generatePostContent(String rawNotes, String postType) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        List<GroqMessage> messages = new ArrayList<>();
        messages.add(new GroqMessage("system",
            "You are an AI Communications Assistant for an Alumni Platform. " +
            "Turn brief raw notes into a professional, engaging " + postType + " announcement. " +
            "Include a catchy title on the first line starting with 'TITLE: ' followed by the complete post body on subsequent lines."
        ));

        messages.add(new GroqMessage("user", "Notes: " + rawNotes));

        GroqRequest request = new GroqRequest("llama-3.3-70b-versatile", messages);
        request.setTemperature(0.7);
        request.setMaxTokens(800);

        String authHeader = "Bearer " + GroqApiClient.GROQ_API_KEY;

        apiService.generateChatCompletion(authHeader, request).enqueue(new Callback<GroqResponse>() {
            @Override
            public void onResponse(Call<GroqResponse> call, Response<GroqResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String content = response.body().getFirstChoiceContent();
                    if (!content.isEmpty()) {
                        result.setValue(Resource.success(content));
                    } else {
                        result.setValue(Resource.error("AI returned empty content.", null));
                    }
                } else {
                    result.setValue(Resource.error("Groq AI Error: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<GroqResponse> call, Throwable t) {
                result.setValue(Resource.error("Network error: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
