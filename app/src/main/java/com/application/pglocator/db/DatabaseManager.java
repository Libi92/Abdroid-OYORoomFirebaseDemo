package com.application.pglocator.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.application.pglocator.model.Feedback;
import com.application.pglocator.model.PGRequest;
import com.application.pglocator.model.PGRoom;
import com.application.pglocator.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class DatabaseManager {
    private static final String USER_DB_PATH = "user";
    private static final String PG_DB_PATH = "pgRoom";
    private static final String PG_REQ_PATH = "pgRequests";
    private static final String FEEDBACK_PATH = "feedback";

    private static final String USER_TYPE = "userType";
    private static final String TAG = DatabaseManager.class.getSimpleName();

    private final DatabaseReference userDbReference;
    private final DatabaseReference pgDbReference;
    private final DatabaseReference requestsDbReference;
    private final DatabaseReference feedbackDbReference;

    private UserListener userListener;
    private PGListener pgListener;
    private PGRequestListener requestListener;

    public DatabaseManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userDbReference = database.getReference(USER_DB_PATH);
        pgDbReference = database.getReference(PG_DB_PATH);
        requestsDbReference = database.getReference(PG_REQ_PATH);
        feedbackDbReference = database.getReference(FEEDBACK_PATH);
    }

    public boolean createUser(User user) {
        return userDbReference.child(user.getUId()).setValue(user).isSuccessful();
    }

    public void getUser(User user) {

        userDbReference.child(user.getUId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: " + snapshot);
                if (userListener != null) {
                    if (snapshot.getValue() != null) {
                        user.setUserType(snapshot.child(USER_TYPE).getValue(String.class));
                        user.setAddress(snapshot.child("address").getValue(String.class));
                        user.setDisplayName(snapshot.child("displayName").getValue(String.class));
                        user.setEmail(snapshot.child("email").getValue(String.class));
                        user.setPhone(snapshot.child("phone").getValue(String.class));
                        user.setRegistered(true);
                    } else {
                        user.setRegistered(false);
                    }
                    userListener.onGetUser(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getDetails());
            }
        });
    }

    public boolean createPG(PGRoom pgRoom) {
        String uId = pgRoom.getUId();
        if (uId == null) {
            uId = UUID.randomUUID().toString();
            pgRoom.setUId(uId);
        }
        return pgDbReference.child(uId).setValue(pgRoom).isSuccessful();
    }

    @SuppressWarnings("unchecked")
    public void getPG(User user) {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (pgListener != null) {
                    HashMap<String, HashMap> snapshotValue = (HashMap<String, HashMap>) snapshot.getValue();
                    if (snapshotValue != null) {
                        Collection<HashMap> values = snapshotValue.values();
                        ArrayList<PGRoom> pgRooms = new ArrayList<>();
                        for (HashMap map : values) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(map);
                            PGRoom pgRoom = gson.fromJson(jsonElement, PGRoom.class);
                            pgRooms.add(pgRoom);
                        }
                        pgListener.onGetPG(pgRooms);
                    }
                } else {
                    Log.e(TAG, "onDataChange: pgListener null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getDetails());
            }
        };

        if (user != null) {
            pgDbReference.orderByChild("userId").equalTo(user.getUId())
                    .addValueEventListener(valueEventListener);
        } else {
            pgDbReference.addValueEventListener(valueEventListener);
        }
    }

    public void createRequest(PGRequest pgRequest) {
        String uId = pgRequest.getUId();
        if (uId == null) {
            uId = UUID.randomUUID().toString();
            pgRequest.setUId(uId);
        }
        requestsDbReference.child(uId).setValue(pgRequest);
    }

    public void createFeedback(Feedback feedback) {
        String uId = feedback.getUId();
        if (uId == null) {
            uId = UUID.randomUUID().toString();
            feedback.setUId(uId);
        }
        feedbackDbReference.child(uId).setValue(feedback);
    }

    public void getPGRequests(String requestedUserId, String targetUserId) {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (requestListener != null) {
                    HashMap<String, HashMap> snapshotValue = (HashMap<String, HashMap>) snapshot.getValue();
                    if (snapshotValue != null) {
                        Collection<HashMap> values = snapshotValue.values();
                        ArrayList<PGRequest> pgRequests = new ArrayList<>();
                        for (HashMap map : values) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(map);
                            PGRequest request = gson.fromJson(jsonElement, PGRequest.class);
                            pgRequests.add(request);
                        }
                        requestListener.onGetPGRequest(pgRequests);
                    }
                } else {
                    Log.e(TAG, "onDataChange: pgListener null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getDetails());
            }
        };

        if (requestedUserId != null) {
            requestsDbReference.orderByChild("requestUserId").equalTo(requestedUserId).addValueEventListener(eventListener);
        } else if (targetUserId != null) {
            requestsDbReference.orderByChild("targetUserId").equalTo(targetUserId).addValueEventListener(eventListener);
        } else {
            requestsDbReference.addValueEventListener(eventListener);
        }
    }

    public static class Builder {
        private final DatabaseManager databaseManager;

        public Builder() {
            databaseManager = new DatabaseManager();
        }

        public Builder userListener(UserListener userListener) {
            databaseManager.userListener = userListener;
            return this;
        }

        public Builder pgListener(PGListener pgListener) {
            databaseManager.pgListener = pgListener;
            return this;
        }

        public Builder requestListener(PGRequestListener requestListener) {
            databaseManager.requestListener = requestListener;
            return this;
        }

        public DatabaseManager build() {
            return this.databaseManager;
        }
    }
}
