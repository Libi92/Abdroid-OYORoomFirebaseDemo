package com.application.pglocator.auth;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.application.pglocator.db.DatabaseManager;
import com.application.pglocator.model.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class AuthManager {
    private static final String TAG = AuthManager.class.getSimpleName();
    private final DatabaseManager databaseManager;
    private Activity activity;
    private int loginReqCode;
    private AuthListener authListener;

    public AuthManager() {
        databaseManager = new DatabaseManager.Builder()
                .userListener(user -> {
                    if (user.isRegistered()) {
                        authListener.onLoginSuccess(user);
                    } else {
                        authListener.registerUser(user);
                    }
                })
                .build();
    }

    public void doLogin() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        this.activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                this.loginReqCode);
    }

    public void doLogout() {
        AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener(task -> {
                    if (authListener != null) {
                        authListener.onLogout();
                    }
                });
    }

    public void handleLoginResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == loginReqCode) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == Activity.RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.i(TAG, "handleLoginResult: " + user.getDisplayName());

                if (authListener != null) {
                    User userModel = new User(user);
                    if (databaseManager != null) {
                        databaseManager.getUser(userModel);
                    }
                }

            } else {
                if (authListener != null) {
                    authListener.onLoginFailed();
                }
                Log.e(TAG, "handleLoginResult: " + response);
            }
        }
    }

    public boolean createUser(User user) {
        return databaseManager.createUser(user);
    }

    public static class Builder {
        private final AuthManager authManager;

        public Builder() {
            authManager = new AuthManager();
        }

        public Builder activity(Activity activity) {
            authManager.activity = activity;
            return this;
        }

        public Builder loginReqCode(int loginReqCode) {
            authManager.loginReqCode = loginReqCode;
            return this;
        }

        public Builder authListener(AuthListener authListener) {
            authManager.authListener = authListener;
            return this;
        }

        public AuthManager build() {
            return this.authManager;
        }
    }
}
