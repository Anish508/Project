package com.alumni.connect.data.api;

import com.alumni.connect.data.model.AdminProfile;
import com.alumni.connect.data.model.AlumniProfile;
import com.alumni.connect.data.model.Event;
import com.alumni.connect.data.model.Job;
import com.alumni.connect.data.model.MentorshipRequest;
import com.alumni.connect.data.model.Post;
import com.alumni.connect.data.model.StudentProfile;
import com.alumni.connect.data.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseDbService {

    // USERS Table
    @POST("rest/v1/users")
    Call<List<User>> createUser(@Body User user);

    @GET("rest/v1/users")
    Call<List<User>> getUsers(@Query("id") String idQuery);

    @GET("rest/v1/users")
    Call<List<User>> getAllUsers();

    @PATCH("rest/v1/users")
    Call<List<User>> updateUser(@Query("id") String idQuery, @Body Map<String, Object> updates);

    // STUDENT PROFILES
    @POST("rest/v1/student_profiles")
    Call<List<StudentProfile>> createStudentProfile(@Body StudentProfile profile);

    @GET("rest/v1/student_profiles")
    Call<List<StudentProfile>> getStudentProfile(@Query("user_id") String userIdQuery);

    // ALUMNI PROFILES
    @POST("rest/v1/alumni_profiles")
    Call<List<AlumniProfile>> createAlumniProfile(@Body AlumniProfile profile);

    @GET("rest/v1/alumni_profiles?select=*,users(*)")
    Call<List<AlumniProfile>> getAllAlumniProfiles();

    @GET("rest/v1/alumni_profiles?select=*,users(*)")
    Call<List<AlumniProfile>> getAlumniProfileByUserId(@Query("user_id") String userIdQuery);

    // ADMIN PROFILES
    @POST("rest/v1/admin_profiles")
    Call<List<AdminProfile>> createAdminProfile(@Body AdminProfile profile);

    // JOBS
    @GET("rest/v1/jobs?order=created_at.desc")
    Call<List<Job>> getJobs();

    @POST("rest/v1/jobs")
    Call<List<Job>> createJob(@Body Job job);

    // EVENTS
    @GET("rest/v1/events?order=created_at.desc")
    Call<List<Event>> getEvents();

    @POST("rest/v1/events")
    Call<List<Event>> createEvent(@Body Event event);

    // MENTORSHIP REQUESTS
    @GET("rest/v1/mentorship_requests?order=created_at.desc")
    Call<List<MentorshipRequest>> getMentorshipRequestsForUser(@Query("mentor_id") String mentorIdQuery);

    @POST("rest/v1/mentorship_requests")
    Call<List<MentorshipRequest>> createMentorshipRequest(@Body MentorshipRequest request);

    @PATCH("rest/v1/mentorship_requests")
    Call<List<MentorshipRequest>> updateMentorshipStatus(@Query("id") String idQuery, @Body Map<String, Object> body);

    // POSTS
    @GET("rest/v1/posts?order=created_at.desc")
    Call<List<Post>> getPosts();

    @POST("rest/v1/posts")
    Call<List<Post>> createPost(@Body Post post);
}
