package com.alumni.connect.data.api;

import com.alumni.connect.data.model.AdminProfile;
import com.alumni.connect.data.model.AlumniProfile;
import com.alumni.connect.data.model.Announcement;
import com.alumni.connect.data.model.Connection;
import com.alumni.connect.data.model.Event;
import com.alumni.connect.data.model.EventRegistration;
import com.alumni.connect.data.model.Job;
import com.alumni.connect.data.model.JobApplication;
import com.alumni.connect.data.model.MentorshipRequest;
import com.alumni.connect.data.model.Post;
import com.alumni.connect.data.model.SavedJob;
import com.alumni.connect.data.model.StudentProfile;
import com.alumni.connect.data.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseDbService {

    // ==================== USERS ====================
    @POST("rest/v1/users")
    Call<List<User>> createUser(@Body User user);

    @GET("rest/v1/users")
    Call<List<User>> getUsers(@Query("id") String idQuery);

    @GET("rest/v1/users")
    Call<List<User>> getUserByEmail(@Query("email") String emailQuery);

    @GET("rest/v1/users")
    Call<List<User>> getAllUsers();

    @GET("rest/v1/users?order=created_at.desc")
    Call<List<User>> getUsersByRole(@Query("role") String roleQuery);

    @PATCH("rest/v1/users")
    Call<List<User>> updateUser(@Query("id") String idQuery, @Body Map<String, Object> updates);

    @DELETE("rest/v1/users")
    Call<Void> deleteUser(@Query("id") String idQuery);

    // ==================== STUDENT PROFILES ====================
    @POST("rest/v1/student_profiles")
    Call<List<StudentProfile>> createStudentProfile(@Body StudentProfile profile);

    @GET("rest/v1/student_profiles?select=*,users(*)")
    Call<List<StudentProfile>> getAllStudentProfiles();

    @GET("rest/v1/student_profiles")
    Call<List<StudentProfile>> getStudentProfile(@Query("user_id") String userIdQuery);

    @PATCH("rest/v1/student_profiles")
    Call<List<StudentProfile>> updateStudentProfile(@Query("user_id") String userIdQuery, @Body Map<String, Object> updates);

    // ==================== ALUMNI PROFILES ====================
    @POST("rest/v1/alumni_profiles")
    Call<List<AlumniProfile>> createAlumniProfile(@Body AlumniProfile profile);

    @GET("rest/v1/alumni_profiles?select=*,users(*)")
    Call<List<AlumniProfile>> getAllAlumniProfiles();

    @GET("rest/v1/alumni_profiles?select=*,users(*)")
    Call<List<AlumniProfile>> getAlumniProfileByUserId(@Query("user_id") String userIdQuery);

    @GET("rest/v1/alumni_profiles?select=*,users(*)&available_for_mentorship=eq.true")
    Call<List<AlumniProfile>> getAvailableMentors();

    @PATCH("rest/v1/alumni_profiles")
    Call<List<AlumniProfile>> updateAlumniProfile(@Query("user_id") String userIdQuery, @Body Map<String, Object> updates);

    // ==================== ADMIN PROFILES ====================
    @POST("rest/v1/admin_profiles")
    Call<List<AdminProfile>> createAdminProfile(@Body AdminProfile profile);

    @GET("rest/v1/admin_profiles")
    Call<List<AdminProfile>> getAdminProfile(@Query("user_id") String userIdQuery);

    // ==================== JOBS ====================
    @GET("rest/v1/jobs?order=created_at.desc")
    Call<List<Job>> getJobs();

    @GET("rest/v1/jobs?order=created_at.desc")
    Call<List<Job>> getJobsByUser(@Query("posted_by") String postedByQuery);

    @POST("rest/v1/jobs")
    Call<List<Job>> createJob(@Body Job job);

    @PATCH("rest/v1/jobs")
    Call<List<Job>> updateJob(@Query("id") String idQuery, @Body Map<String, Object> updates);

    @DELETE("rest/v1/jobs")
    Call<Void> deleteJob(@Query("id") String idQuery);

    // ==================== EVENTS ====================
    @GET("rest/v1/events?order=created_at.desc")
    Call<List<Event>> getEvents();

    @POST("rest/v1/events")
    Call<List<Event>> createEvent(@Body Event event);

    @PATCH("rest/v1/events")
    Call<List<Event>> updateEvent(@Query("id") String idQuery, @Body Map<String, Object> updates);

    @DELETE("rest/v1/events")
    Call<Void> deleteEvent(@Query("id") String idQuery);

    // ==================== MENTORSHIP REQUESTS ====================
    @GET("rest/v1/mentorship_requests?select=*,mentor:mentor_id(*),mentee:mentee_id(*)&order=created_at.desc")
    Call<List<MentorshipRequest>> getMentorshipRequestsForUserOrMentee(@Query("or") String orQuery);

    @GET("rest/v1/mentorship_requests?order=created_at.desc")
    Call<List<MentorshipRequest>> getMentorshipRequestsForUser(@Query("mentor_id") String mentorIdQuery);

    @GET("rest/v1/mentorship_requests?order=created_at.desc")
    Call<List<MentorshipRequest>> getMentorshipRequestsByMentee(@Query("mentee_id") String menteeIdQuery);

    @POST("rest/v1/mentorship_requests")
    Call<List<MentorshipRequest>> createMentorshipRequest(@Body MentorshipRequest request);

    @PATCH("rest/v1/mentorship_requests")
    Call<List<MentorshipRequest>> updateMentorshipStatus(@Query("id") String idQuery, @Body Map<String, Object> body);

    // ==================== POSTS ====================
    @GET("rest/v1/posts?order=created_at.desc")
    Call<List<Post>> getPosts();

    @POST("rest/v1/posts")
    Call<List<Post>> createPost(@Body Post post);

    @PATCH("rest/v1/posts")
    Call<List<Post>> updatePost(@Query("id") String idQuery, @Body Map<String, Object> updates);

    @DELETE("rest/v1/posts")
    Call<Void> deletePost(@Query("id") String idQuery);

    // ==================== CONNECTIONS ====================
    @POST("rest/v1/connections")
    Call<List<Connection>> createConnection(@Body Connection connection);

    @GET("rest/v1/connections?order=created_at.desc")
    Call<List<Connection>> getConnectionsForUser(@Query("or") String orQuery);

    @PATCH("rest/v1/connections")
    Call<List<Connection>> updateConnectionStatus(@Query("id") String idQuery, @Body Map<String, Object> updates);

    @DELETE("rest/v1/connections")
    Call<Void> deleteConnection(@Query("id") String idQuery);

    // ==================== SAVED JOBS ====================
    @POST("rest/v1/saved_jobs")
    Call<List<SavedJob>> saveJob(@Body SavedJob savedJob);

    @GET("rest/v1/saved_jobs")
    Call<List<SavedJob>> getSavedJobs(@Query("user_id") String userIdQuery);

    @DELETE("rest/v1/saved_jobs")
    Call<Void> unsaveJob(@Query("id") String idQuery);

    // ==================== EVENT REGISTRATIONS ====================
    @POST("rest/v1/event_registrations")
    Call<List<EventRegistration>> registerForEvent(@Body EventRegistration registration);

    @GET("rest/v1/event_registrations")
    Call<List<EventRegistration>> getEventRegistrations(@Query("event_id") String eventIdQuery);

    @GET("rest/v1/event_registrations")
    Call<List<EventRegistration>> getUserEventRegistrations(@Query("user_id") String userIdQuery);

    // ==================== ANNOUNCEMENTS ====================
    @POST("rest/v1/announcements")
    Call<List<Announcement>> createAnnouncement(@Body Announcement announcement);

    @GET("rest/v1/announcements?order=created_at.desc")
    Call<List<Announcement>> getAnnouncements();

    @PATCH("rest/v1/announcements")
    Call<List<Announcement>> updateAnnouncement(@Query("id") String idQuery, @Body Map<String, Object> updates);

    @DELETE("rest/v1/announcements")
    Call<Void> deleteAnnouncement(@Query("id") String idQuery);

    // ==================== JOB APPLICATIONS ====================
    @POST("rest/v1/job_applications")
    Call<List<JobApplication>> createJobApplication(@Body JobApplication application);

    @GET("rest/v1/job_applications?order=created_at.desc")
    Call<List<JobApplication>> getJobApplicationsByJobId(@Query("job_id") String jobIdQuery);

    @GET("rest/v1/job_applications?order=created_at.desc")
    Call<List<JobApplication>> getJobApplicationsByPoster(@Query("posted_by") String postedByQuery);

    @GET("rest/v1/job_applications?order=created_at.desc")
    Call<List<JobApplication>> getAllJobApplications();

    @DELETE("rest/v1/job_applications")
    Call<Void> deleteJobApplication(@Query("id") String idQuery);
}

