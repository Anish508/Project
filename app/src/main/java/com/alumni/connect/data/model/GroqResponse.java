package com.alumni.connect.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroqResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("choices")
    private List<Choice> choices;

    public String getId() { return id; }
    public List<Choice> getChoices() { return choices; }

    public String getFirstChoiceContent() {
        if (choices != null && !choices.isEmpty() && choices.get(0).getMessage() != null) {
            return choices.get(0).getMessage().getContent();
        }
        return "";
    }

    public static class Choice {
        @SerializedName("index")
        private int index;

        @SerializedName("message")
        private GroqMessage message;

        public int getIndex() { return index; }
        public GroqMessage getMessage() { return message; }
    }
}
