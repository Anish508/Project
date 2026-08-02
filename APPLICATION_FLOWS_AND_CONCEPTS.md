# Alumni Networking Platform: Application Flows & Core Concepts

This document explains the end-to-end user and system flows within the application and details the key Android software engineering concepts used in the codebase.

---

## 1. End-to-End Application Flows

### Flow A: Authentication & Role-Based Authorization
1. **User Launch**: `SplashActivity` checks `SessionManager.isLoggedIn()`.
   * If logged in, redirects immediately to `MainActivity` or `AdminMainActivity` based on `sessionManager.getRole()`.
   * If not logged in, navigates to `AuthActivity` (`LoginFragment`).
2. **Login Processing**:
   * User enters credentials.
   * `AuthRepository.login()` executes a Retrofit query to Supabase Auth / PostgreSQL `public.users` table (`getUserByEmail`).
   * **Strict Validation**: If email does NOT exist in the database, `Resource.error("Account not found. Please register first.")` is returned to the UI.
   * If user is found, `SessionManager.saveSession()` persists credentials locally, and user navigates to the role dashboard.
3. **Registration & Profile Creation**:
   * User selects role (`Student` or `Alumni`), enters details (Department, Batch Year, Company, etc.).
   * `AuthRepository.register()` creates the `User` entry in `public.users` table, then inserts a corresponding role profile record into `student_profiles` or `alumni_profiles`.
   * **Alumni Verification**: Alumni accounts receive `is_verified = false` by default until an Administrator approves their profile via `UserManagementFragment`.

---

### Flow B: Community Feed & Complete Post Reading
1. **Feed Loading**:
   * `HomeFragment` subscribes to `HomeViewModel.getPosts()`.
   * `PostRepository` fetches posts from Supabase PostgreSQL table (`public.posts`).
   * Posts are rendered in `rvPosts` using `PostAdapter`.
2. **Post Card Interaction**:
   * `PostAdapter` holds an `OnPostClickListener`.
   * Tapping any post item triggers `onPostClick(post)`.
   * `HomeFragment` instantiates `PostDetailDialogFragment.newInstance(post)`.
3. **Post Detail Viewing**:
   * `PostDetailDialogFragment` opens as a Material BottomSheet.
   * Renders the complete un-truncated Title, Post Type Badge (ANNOUNCEMENT / DISCUSSION), Author Info, Date, and full scrollable content body in a `NestedScrollView`.

---

### Flow C: Job Searching, AI Resume Matching & Job Application
1. **Jobs Directory**:
   * `JobsFragment` retrieves all active job listings from `JobRepository`.
   * Users can filter by title, company, location, or required skills using the search bar.
2. **Groq AI Resume-to-Job Matcher**:
   * Student taps the **"AI Match"** button (`btnAiMatch`) on any job card.
   * `JobsFragment` launches `AiAdvisorDialogFragment.newInstance(jobTitle, jobDescription)`.
   * Student enters their current skills (or leaves default).
   * `AiAdvisorRepository` calls Groq API (`https://api.groq.com/openai/v1/chat/completions`) using model `llama-3.3-70b-versatile`.
   * The AI response is parsed and formatted dynamically in Markdown:
     * **Match Score %**
     * **Strongest Qualifications**
     * **Missing Requirements & Skill Gaps**
     * **3 Actionable Resume Tips**
3. **Job Application**:
   * Student taps **"Apply Now"** (`btnApply`).
   * `ApplyJobDialogFragment` opens, allows cover note entry, and submits `JobApplication` to Supabase.

---

### Flow D: AI Post & Job Announcement Generation
1. **Alumni/Admin Post Creation**:
   * User navigates to `CreatePostFragment` or `CreateJobFragment`.
   * Enters brief bullet notes (e.g., *"Hiring 2 Java interns, remote, 6 months"*).
   * Taps **"Enhance / Generate with Groq AI"**.
2. **AI Inference & Form Auto-Fill**:
   * `AiAdvisorRepository.generatePostContent()` sends the notes to Groq LLM.
   * Groq expands the notes into a structured title and comprehensive announcement body.
   * `CreatePostFragment` / `CreateJobFragment` automatically populates `etPostTitle` and `etPostContent` with the generated text.

---

## 2. Core Concepts & Architecture Explained

### 1. JSON Serialization & Deserialization (`@SerializedName`)
* **What it is**: Serialization converts Java objects into JSON strings for API network transmission. Deserialization converts JSON responses back into Java objects.
* **How it is used**: We use Google **Gson** with Retrofit. Java model properties are annotated with `@SerializedName("json_key_name")`.
* **Example**:
  ```java
  public class User {
      @SerializedName("id")
      private String id;

      @SerializedName("is_verified")
      private boolean isVerified;
  }
  ```
  This ensures that when Supabase returns `{"is_verified": true}`, Gson maps it directly to the Java boolean `isVerified`.

---

### 2. MVVM (Model-View-ViewModel) Architecture
* **Model**: Data objects, Database schema, and API services (`data/` package).
* **View**: UI components (`Fragment`, `Activity`, XML Layouts) that display data and capture user gestures. Views contain **no business logic**.
* **ViewModel**: Preserves state across configuration changes (e.g. screen rotation), handles UI logic, and exposes observable `LiveData` from Repositories.

```
+------------------+         Observes LiveData        +------------------+
|   UI (Fragment)  | <------------------------------- |    ViewModel     |
+------------------+                                  +------------------+
         |                                                     |
         | User Actions                                        | Fetches Data
         v                                                     v
+------------------+       Calls Retrofit API         +------------------+
|  ViewBinding/XML |                                  |    Repository    |
+------------------+                                  +------------------+
                                                               |
                                                               v
                                                      +------------------+
                                                      | Remote DB / Groq |
                                                      +------------------+
```

---

### 3. LiveData & Reactive UI Updates
* **LiveData** is an lifecycle-aware observable data holder class.
* Unlike a regular observable, LiveData respects the lifecycle of Android components (`Fragments` / `Activities`), ensuring UI components only update when in an active state (`STARTED` or `RESUMED`), preventing memory leaks and null-pointer crashes.
* **Usage**:
  ```java
  viewModel.getPosts().observe(getViewLifecycleOwner(), resource -> {
      if (resource.status == Resource.Status.SUCCESS) {
          adapter.setPosts(resource.data);
      }
  });
  ```

---

### 4. ViewBinding
* **ViewBinding** generates a binding class for every XML layout file (e.g., `FragmentHomeBinding` for `fragment_home.xml`).
* It replaces error-prone `findViewById()` calls with type-safe, null-safe direct properties (`binding.tvWelcomeUser`, `binding.rvPosts`).

---

### 5. Repository Pattern & Clean Architecture
* The Repository acts as a single source of truth for data operations. It abstracts whether data comes from remote REST APIs (Supabase, Groq) or local storage (`SessionManager`).
* UI components do not know or care how data is fetched; they simply observe LiveData emitted by the Repository.

---

### 6. SharedPreferences & Session Management
* `SessionManager.java` uses `SharedPreferences` to store encrypted key-value pairs (`user_id`, `email`, `role`, `auth_token`).
* This enables persistent login sessions across application restarts so users do not need to enter credentials every time the app opens.
