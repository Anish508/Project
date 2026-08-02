# Groq AI Integration Plan: Alumni Career & Content Assistant

## 1. Executive Summary
This document presents a lightweight, achievable, high-value AI feature proposal for the **Alumni Networking Platform** using **Groq API** (`llama-3.3-70b-versatile`). The feature enhances student career readiness and simplifies post creation for alumni and administrators with minimal code complexity.

---

## 2. Proposed AI Features

### Feature A: AI Resume-to-Job Matcher & Career Advisor (Student View)
* **Goal**: Enable students to assess their fit for alumni-posted jobs and receive instant feedback.
* **Flow**:
  1. Student opens any Job opportunity in the app.
  2. Clicks **"AI Match & Resume Review"**.
  3. Student enters key skills / experience summary or auto-fills from profile.
  4. Groq LLM analyzes the job description against student profile.
  5. Displays:
     * **Match Score** (0 - 100%)
     * **Missing Skills & Gaps**
     * **3 Actionable Resume Recommendations** to improve application chances.

### Feature B: AI Post & Job Announcement Drafting (Alumni & Admin View)
* **Goal**: Help Alumni and Admins generate clean, professional posts and job listings quickly.
* **Flow**:
  1. User enters 2–3 brief bullet points (e.g. *"Hiring Java developer intern, remote, 6 months"*).
  2. Clicks **"Generate with AI"**.
  3. Groq LLM converts raw notes into a polished announcement with clear title, qualifications, and call-to-action.

---

## 3. Groq API Technical Architecture

### Endpoint Details
* **Provider**: Groq Cloud API (`https://api.groq.com/openai/v1/chat/completions`)
* **Model**: `llama-3.3-70b-versatile`
* **Authentication**: Bearer Token via Groq API Key (`GROQ_API_KEY`)
* **Latency**: ~300ms response time (ultra-fast inference)

### API Request Schema (Retrofit / OKHttp)
```json
{
  "model": "llama-3.3-70b-versatile",
  "messages": [
    {
      "role": "system",
      "content": "You are an AI career advisor for an Alumni Networking Platform. Return response in concise Markdown format."
    },
    {
      "role": "user",
      "content": "Analyze student skills [Java, SQL, Android] against Job Description [Android Dev Intern at TechCorp]. Give match percentage, gaps, and tips."
    }
  ],
  "temperature": 0.5,
  "max_tokens": 512
}
```

---

## 4. Android Implementation Architecture

### Components to Create / Modify
1. **`GroqApiService.java`**: Retrofit interface for Groq OpenAI-compatible endpoints.
2. **`GroqApiClient.java`**: Retrofit singleton client configured with headers (`Authorization: Bearer <API_KEY>`).
3. **`AiAdvisorRepository.java`**: Handles prompt construction, network calls, and LiveData wrapping.
4. **`AiAdvisorDialogFragment.java`**: Material BottomSheet dialog for seamless UI interaction.
5. **`fragment_ai_advisor.xml`**: Clean Material UI card layout displaying loading indicators, match scores, and markdown suggestions.

---

## 5. Security & Configuration
* **API Key Management**: Store API Key securely in `local.properties` or `BuildConfig` (`GROQ_API_KEY="YOUR_KEY"`).
* **Rate Limit Handling**: Implement client-side error handling for 429/500 responses with clear user toasts.

---

## 6. Implementation Checklist
- [ ] Add `GroqApiService` & `GroqApiClient` network layer
- [ ] Add AI prompt templates for Resume Match & Post Formatting
- [ ] Create `AiAdvisorDialogFragment` & XML layout
- [ ] Connect "AI Match" button in Job Detail dialog
- [ ] Connect "AI Generate" button in Create Job & Create Post fragments
