package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroqRequest {
    @SerializedName("model")
    private String model = "llama-3.3-70b-versatile";

    @SerializedName("messages")
    private List<GroqMessage> messages;

    @SerializedName("temperature")
    private double temperature = 0.5;

    @SerializedName("max_tokens")
    private int maxTokens = 1024;

    public GroqRequest() {}

    public GroqRequest(String model, List<GroqMessage> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<GroqMessage> getMessages() { return messages; }
    public void setMessages(List<GroqMessage> messages) { this.messages = messages; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
}
