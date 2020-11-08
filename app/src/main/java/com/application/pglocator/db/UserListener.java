package com.application.pglocator.db;

import com.application.pglocator.model.User;

import java.util.List;

public interface UserListener {
    void onGetUser(User user);

    void onListUser(List<User> users);
}
