package com.application.pglocator.model;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User implements Serializable {
    private String photo;
    private String uId;
    private String userType;
    private String displayName;
    private String email;
    private boolean isRegistered;
    private String phone;
    private String address;
    private String Status;

    public User(FirebaseUser firebaseUser) {
        this.uId = firebaseUser.getUid();
        Uri photoUrl = firebaseUser.getPhotoUrl();
        if (photoUrl != null) {
            this.photo = photoUrl.toString();
        }
        this.displayName = firebaseUser.getDisplayName();
        this.email = firebaseUser.getEmail();
    }
}
