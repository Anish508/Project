package com.alumni.connect.ui.mentorship;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alumni.connect.data.model.Event;
import com.alumni.connect.data.model.MentorshipRequest;
import com.alumni.connect.data.repository.EventRepository;
import com.alumni.connect.data.repository.MentorshipRepository;
import com.alumni.connect.util.Resource;

import java.util.List;

public class MentorshipViewModel extends AndroidViewModel {
    private final MentorshipRepository mentorshipRepository;
    private final EventRepository eventRepository;

    public MentorshipViewModel(@NonNull Application application) {
        super(application);
        this.mentorshipRepository = new MentorshipRepository(application);
        this.eventRepository = new EventRepository(application);
    }

    public LiveData<Resource<List<MentorshipRequest>>> getMentorshipRequests(String userId) {
        return mentorshipRepository.getRequestsForUser(userId);
    }

    public LiveData<Resource<Boolean>> updateRequestStatus(String requestId, String status) {
        return mentorshipRepository.updateStatus(requestId, status);
    }

    public LiveData<Resource<List<Event>>> getEvents() {
        return eventRepository.getEvents();
    }
}
