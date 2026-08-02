# Alumni Networking Platform: Database Schema & API Documentation

This document specifies the PostgreSQL database schema, Supabase PostgREST query parameters, Groq AI API integrations, and developer setup instructions for the **Alumni Networking Platform**.

---

## 1. Supabase PostgreSQL Database Schema

The platform relies on a relational PostgreSQL database configured on Supabase. Below are the primary tables and their relationships:

```
                      +-------------------+
                      |       users       |
                      +-------------------+
                      | id (PK, UUID)     |
                      | email (UNIQUE)    |
                      | role              |
                      | full_name         |
                      | is_verified       |
                      | is_active         |
                      +-------------------+
                               |
         +---------------------+---------------------+
         |                     |                     |
         v                     v                     v
+------------------+  +------------------+  +------------------+
| student_profiles |  | alumni_profiles  |  |  admin_profiles  |
+------------------+  +------------------+  +------------------+
| user_id (FK)     |  | user_id (FK)     |  | user_id (FK)     |
| department       |  | graduation_year  |  | department       |
| batch_year       |  | company          |  +------------------+
| skills           |  | designation      |
+------------------+  +------------------+
         |                     |
         +----------+----------+
                    |
                    v
          +-------------------+
          |       jobs        |
          +-------------------+
          | id (PK, UUID)     |
          | posted_by (FK)    |
          | title             |
          | company           |
          | description       |
          | skills_required   |
          +-------------------+
                    |
                    v
          +-------------------+
          |  job_applications |
          +-------------------+
          | id (PK, UUID)     |
          | job_id (FK)       |
          | applicant_id (FK) |
          | cover_note        |
          | status            |
          +-------------------+
```

### Table Definitions

#### `users`
* `id`: `UUID` (Primary Key, default `gen_random_uuid()`)
* `email`: `VARCHAR` (Unique, Not Null)
* `role`: `VARCHAR` (`student`, `alumni`, `admin`)
* `full_name`: `VARCHAR` (Not Null)
* `avatar_url`: `TEXT`
* `is_verified`: `BOOLEAN` (Default `true` for students, `false` for alumni)
* `is_active`: `BOOLEAN` (Default `true`)
* `created_at`: `TIMESTAMPTZ` (Default `now()`)

#### `student_profiles`
* `id`: `UUID` (Primary Key)
* `user_id`: `UUID` (Foreign Key -> `users.id` ON DELETE CASCADE)
* `department`: `VARCHAR`
* `batch_year`: `INT`
* `current_semester`: `INT`
* `skills`: `TEXT`
* `bio`: `TEXT`

#### `alumni_profiles`
* `id`: `UUID` (Primary Key)
* `user_id`: `UUID` (Foreign Key -> `users.id` ON DELETE CASCADE)
* `department`: `VARCHAR`
* `graduation_year`: `INT`
* `current_company`: `VARCHAR`
* `designation`: `VARCHAR`
* `linkedin_url`: `TEXT`
* `is_available_for_mentorship`: `BOOLEAN` (Default `true`)

#### `posts`
* `id`: `UUID` (Primary Key)
* `author_id`: `UUID` (Foreign Key -> `users.id`)
* `title`: `TEXT` (Not Null)
* `content`: `TEXT` (Not Null)
* `post_type`: `VARCHAR` (`announcement`, `discussion`, `event`)
* `created_at`: `TIMESTAMPTZ`

#### `jobs`
* `id`: `UUID` (Primary Key)
* `posted_by`: `UUID` (Foreign Key -> `users.id`)
* `title`: `VARCHAR` (Not Null)
* `company`: `VARCHAR` (Not Null)
* `location`: `VARCHAR`
* `job_type`: `VARCHAR` (`Full-time`, `Part-time`, `Internship`, `Remote`)
* `salary_range`: `VARCHAR`
* `eligibility`: `TEXT`
* `skills_required`: `TEXT`
* `description`: `TEXT` (Not Null)
* `target_audience`: `VARCHAR` (`all`, `student`, `alumni`)
* `created_at`: `TIMESTAMPTZ`

#### `job_applications`
* `id`: `UUID` (Primary Key)
* `job_id`: `UUID` (Foreign Key -> `jobs.id`)
* `applicant_id`: `UUID` (Foreign Key -> `users.id`)
* `cover_note`: `TEXT`
* `status`: `VARCHAR` (`submitted`, `reviewed`, `accepted`, `rejected`)
* `applied_at`: `TIMESTAMPTZ`

#### `mentorship_requests`
* `id`: `UUID` (Primary Key)
* `mentee_id`: `UUID` (Foreign Key -> `users.id`)
* `mentor_id`: `UUID` (Foreign Key -> `users.id`)
* `message`: `TEXT`
* `status`: `VARCHAR` (`pending`, `accepted`, `rejected`)
* `created_at`: `TIMESTAMPTZ`

---

## 2. Supabase PostgREST API Integration

The app connects to Supabase using RESTful PostgREST syntax via Retrofit.

### Header Configuration
Every HTTP request includes headers injected by `HeaderInterceptor.java`:
```http
apikey: <SUPABASE_ANON_KEY>
Authorization: Bearer <SUPABASE_ANON_KEY_OR_USER_TOKEN>
Content-Type: application/json
Prefer: return=representation
```

### Common Query Endpoints (`SupabaseDbService.java`)

1. **User Lookup by Email**:
   `GET /rest/v1/users?email=eq.user@email.com&select=*`
2. **Fetch Verified Alumni Profiles**:
   `GET /rest/v1/alumni_profiles?select=*,user:users(*)&order=graduation_year.desc`
3. **Fetch Community Feed**:
   `GET /rest/v1/posts?select=*&order=created_at.desc`
4. **Publish New Job**:
   `POST /rest/v1/jobs`
5. **Update User Active Status**:
   `PATCH /rest/v1/users?id=eq.<USER_UUID>`
   * Body: `{"is_active": false}`

---

## 3. Groq AI Integration API

The application utilizes **Groq Cloud API** for ultra-fast LLM inference (`llama-3.3-70b-versatile`).

### Endpoint Specification
* **URL**: `https://api.groq.com/openai/v1/chat/completions`
* **HTTP Method**: `POST`
* **Headers**:
  ```http
  Authorization: Bearer YOUR_GROQ_API_KEY_HERE
  Content-Type: application/json
  ```

### Sample Payload Format (`GroqRequest`)
```json
{
  "model": "llama-3.3-70b-versatile",
  "temperature": 0.4,
  "max_tokens": 1024,
  "messages": [
    {
      "role": "system",
      "content": "You are an expert AI Career Advisor. Analyze candidate skills against job requirements."
    },
    {
      "role": "user",
      "content": "Job Title: Android Dev\nSkills: Java, SQL, MVVM"
    }
  ]
}
```

---

## 4. Developer Setup & Build Guide

### Prerequisites
* **Android Studio**: Jellyfish / Koala / Ladybug or newer.
* **JDK**: OpenJDK 17.
* **Minimum Android SDK**: API 24 (Android 7.0).
* **Target & Compile SDK**: API 30 (Android 11).

### Building and Running the App
1. Clone the project repository into your workspace.
2. Open the project folder in **Android Studio**.
3. Allow Gradle sync to download dependencies (Retrofit, Gson, Material Components, Navigation).
4. Run Gradle assemble command from PowerShell / Terminal:
   ```powershell
   .\gradlew.bat assembleDebug
   ```
5. Deploy `app-debug.apk` to an Android Emulator (Google Pixel 4a API 30 recommended) or physical device.
