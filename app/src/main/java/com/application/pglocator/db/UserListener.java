package com.application.pglocator.db;

import com.application.pglocator.model.User;

public interface UserListener {
    void onGetUser(User user);
}
