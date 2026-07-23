package com.alumni.connect.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alumni.connect.data.model.User;
import com.alumni.connect.data.repository.AuthRepository;
import com.alumni.connect.util.Resource;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository(application);
    }

    public LiveData<Resource<User>> login(String email, String password) {
        return authRepository.login(email, password);
    }

    public LiveData<Resource<User>> register(String email, String password, String fullName, String role,
                                            String department, int year, String company, String designation) {
        return authRepository.register(email, password, fullName, role, department, year, company, designation);
    }

    public LiveData<Resource<String>> resetPassword(String email) {
        return authRepository.resetPassword(email);
    }
}
