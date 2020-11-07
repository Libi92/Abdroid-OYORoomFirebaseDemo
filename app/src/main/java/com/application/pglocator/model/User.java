package com.application.pglocator.model;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User implements Serializable {
    private String uId;
    private String userType;
    private String displayName;
    private String email;
    private boolean isRegistered;
    private String phone;
    private String address;

    public User(FirebaseUser firebaseUser) {
        this.uId = firebaseUser.getUid();
        this.displayName = firebaseUser.getDisplayName();
        this.email = firebaseUser.getEmail();
    }
}
