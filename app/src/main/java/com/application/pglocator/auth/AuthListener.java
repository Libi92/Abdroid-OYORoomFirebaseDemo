package com.application.pglocator.auth;

import com.application.pglocator.model.User;

public interface AuthListener {
    void onLoginSuccess(User user);

    void onLoginFailed();

    void onLogout();

    void registerUser(User user);
}
