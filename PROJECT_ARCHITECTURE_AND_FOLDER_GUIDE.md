# Alumni Networking Platform: Project Architecture & Folder Guide

This document provides a comprehensive breakdown of the project directory structure, package organization, and component roles within the **Alumni Networking Platform** Android application.

---

## 1. Top-Level Directory Overview

```
Project/
├── app/                        # Main Android module source code
│   └── src/
│       └── main/
│           ├── java/com/alumni/connect/
│           │   ├── data/       # Data Layer (API, Local Storage, Models, Repositories)
│           │   ├── ui/         # User Interface Layer (Fragments, Adapters, ViewModels)
│           │   └── util/       # Utility classes, Constants, State wrappers
│           ├── res/            # Resources (Layouts, Navigation Graphs, Drawables, Values)
│           └── AndroidManifest.xml
├── build.gradle                # Project-level Gradle build file
├── settings.gradle             # Module settings
├── schema.sql                  # PostgreSQL database schema script for Supabase
├── ai_feature_plan.md          # Groq AI integration roadmap
└── agent.md                    # System context & target specifications
```

---

## 2. Package-by-Package Breakdown

### `com.alumni.connect.data` (Data Layer)
The Data Layer handles all data acquisition, remote REST communication with Supabase and Groq, local session storage, and data transformation.

#### A. `data.api` (Remote API Clients & Services)
* **`SupabaseClient.java`**: Singleton factory that configures and instantiates Retrofit service interfaces for Supabase PostgreSQL and Authentication. Includes logging and header interceptors.
* **`SupabaseDbService.java`**: Retrofit interface defining PostgREST HTTP operations (`GET`, `POST`, `PATCH`, `DELETE`) for querying relational database tables (`users`, `posts`, `jobs`, `student_profiles`, `alumni_profiles`, `mentorship_requests`, etc.).
* **`SupabaseAuthService.java`**: Retrofit interface for Supabase GoTrue authentication endpoints (`/auth/v1/signup`, `/auth/v1/token?grant_type=password`, `/auth/v1/recover`).
* **`HeaderInterceptor.java`**: OkHttp interceptor that automatically attaches required Supabase `apikey`, `Authorization: Bearer <token>`, and `Prefer: return=representation` headers to outgoing HTTP requests.
* **`GroqApiClient.java`**: Singleton Retrofit client pointing to `https://api.groq.com/` for AI inference calls.
* **`GroqApiService.java`**: Retrofit interface mapping `@POST("openai/v1/chat/completions")` for sending prompts to the Groq LLM engine (`llama-3.3-70b-versatile`).

#### B. `data.local` (Local Storage & Session Management)
* **`SessionManager.java`**: Encapsulates Android `SharedPreferences` to manage persistent user login state. Stores user ID, email, full name, role (`student`, `alumni`, `admin`), and session access tokens across app restarts.

#### C. `data.model` (Data Transfer Objects & POJOs)
Contains Java Data Models decorated with Gson `@SerializedName` annotations for JSON mapping:
* **`User.java`**: Core user account entity containing ID, email, role, full name, profile picture URL, verification status, and active status.
* **`StudentProfile.java`**: Student-specific attributes (department, batch year, current semester, skills, bio).
* **`AlumniProfile.java`**: Alumni-specific attributes (graduation year, company, designation, department, LinkedIn URL).
* **`AdminProfile.java`**: Administrator profile details.
* **`Post.java`**: Community announcements and general posts.
* **`Job.java`**: Job & internship opportunities posted by alumni and admins.
* **`JobApplication.java`**: Applications submitted by students for posted jobs.
* **`MentorshipRequest.java`**: Connection and mentorship requests between students and alumni mentors.
* **`Event.java`**: University events, webinars, and reunions.
* **`SavedJob.java`**: Bookmarked jobs for students.
* **`GroqRequest.java`**, **`GroqMessage.java`**, **`GroqResponse.java`**: Models formatting requests and parsing responses from the Groq OpenAI-compatible API.

#### D. `data.repository` (Repository Pattern)
Repositories mediate between remote API calls and the UI layer. They provide clean LiveData APIs for ViewModels.
* **`AuthRepository.java`**: Handles login, registration, role-specific profile creation, session saving, and authentication validation.
* **`PostRepository.java`**: Handles fetching community posts and publishing new posts.
* **`JobRepository.java`**: Manages job listings, creating job posts, and job filtering.
* **`JobApplicationRepository.java`**: Handles student job submissions and applicant viewing.
* **`MentorshipRepository.java`**: Manages mentorship requests and request status updates (`pending`, `accepted`, `rejected`).
* **`ProfileRepository.java`**: Fetches and updates user profiles for students, alumni, and admins.
* **`AdminRepository.java`**: Handles administrative capabilities (user verification, account suspension, global content moderation).
* **`AiAdvisorRepository.java`**: Formulates AI prompts and manages async inference network calls with Groq Cloud API.

---

### `com.alumni.connect.ui` (User Interface Layer)
Organized into feature subpackages following **MVVM (Model-View-ViewModel)** architecture:

* **`ui.auth`**: `LoginActivity`, `LoginFragment`, `RegisterFragment`, `ForgotPasswordFragment`, `AuthViewModel`. Manages user authentication UI and registration forms.
* **`ui.home`**: `HomeFragment`, `HomeViewModel`, `PostAdapter`, `PostDetailDialogFragment`. Displays welcome card, live stats (Alumni, Jobs, Mentors), community feed, and detail viewing modal.
* **`ui.community`**: `CreatePostFragment`. UI form for publishing community announcements with AI generation support.
* **`ui.jobs`**: `JobsFragment`, `CreateJobFragment`, `JobAdapter`, `ApplyJobDialogFragment`, `ViewApplicationsDialogFragment`, `AiAdvisorDialogFragment`. Manages job searches, applications, applicant review, and AI Resume Matching.
* **`ui.directory`**: `DirectoryFragment`, `AlumniAdapter`, `AlumniDetailFragment`. Searchable directory of verified alumni with department filtering.
* **`ui.mentorship`**: `MentorshipFragment`, `MentorshipAdapter`. Lists available alumni mentors and connection requests.
* **`ui.events`**: `EventsFragment`, `EventAdapter`, `CreateEventFragment`. Lists university events and handles event registrations.
* **`ui.profile`**: `ProfileFragment`, `EditProfileFragment`, `ProfileViewModel`. Displays and edits role-specific profile information.
* **`ui.admin`**: `AdminHomeFragment`, `AdminDashboardFragment`, `UserManagementFragment`, `ContentModerationFragment`, `UserAdapter`. Admin panel for approving alumni, moderating content, and tracking platform metrics.
* **`ui.settings`**: `SettingsFragment`. Password reset, theme toggles, and session logout.

---

### `com.alumni.connect.util` (Utilities)
* **`Constants.java`**: Global string keys (`ROLE_STUDENT`, `ROLE_ALUMNI`, `ROLE_ADMIN`).
* **`Resource.java`**: Generic wrapper class representing data loading states (`SUCCESS`, `ERROR`, `LOADING`).
* **`SupabaseConfig.java`**: Supabase URL and anon API key configuration.

---

## 3. UI Resources (`app/src/main/res/`)

* **`res/layout/`**: XML layout files utilizing Material Design 3 and ConstraintLayout (e.g. `fragment_home.xml`, `item_post.xml`, `dialog_post_detail.xml`, `dialog_ai_advisor.xml`).
* **`res/navigation/`**: Android Navigation Component graphs (`nav_graph_main.xml`, `nav_graph_auth.xml`, `nav_graph_admin.xml`) defining fragment transactions and deep links.
* **`res/drawable/`**: Vector icons (`ic_home.xml`, `ic_ai_sparkles.xml`, `ic_jobs.xml`, `ic_close.xml`) and custom shape backgrounds (`bg_chip.xml`, `bg_rounded_card.xml`).
