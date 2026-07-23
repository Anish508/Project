package com.alumni.connect.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alumni.connect.data.model.User;
import com.alumni.connect.data.repository.ProfileRepository;
import com.alumni.connect.util.Resource;

import java.util.Map;

public class ProfileViewModel extends AndroidViewModel {
    private final ProfileRepository profileRepository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.profileRepository = new ProfileRepository(application);
    }

    public LiveData<Resource<User>> updateProfile(String userId, Map<String, Object> updates) {
        return profileRepository.updateUserProfile(userId, updates);
    }
}
