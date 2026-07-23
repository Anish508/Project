package com.alumni.connect.ui.directory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alumni.connect.data.model.AlumniProfile;
import com.alumni.connect.data.repository.ProfileRepository;
import com.alumni.connect.util.Resource;

import java.util.List;

public class DirectoryViewModel extends AndroidViewModel {
    private final ProfileRepository profileRepository;

    public DirectoryViewModel(@NonNull Application application) {
        super(application);
        this.profileRepository = new ProfileRepository(application);
    }

    public LiveData<Resource<List<AlumniProfile>>> getAlumniProfiles() {
        return profileRepository.getAllAlumniProfiles();
    }
}
