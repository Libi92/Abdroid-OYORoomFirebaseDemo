package com.application.pglocator.db;

import android.util.Log;

import androidx.annotation.NonNull;

import com.application.pglocator.constants.UserState;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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
    private FeedbackListener feedbackListener;

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
                        Set<String> keySet = snapshotValue.keySet();
                        ArrayList<PGRoom> pgRooms = new ArrayList<>();
                        for (String key : keySet) {
                            HashMap map = snapshotValue.get(key);
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(map);
                            PGRoom pgRoom = gson.fromJson(jsonElement, PGRoom.class);
                            pgRoom.setUId(key);
                            pgRooms.add(pgRoom);
                        }

                        mapPGFeedbackUser(pgRooms);
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

    private void mapPGFeedbackUser(List<PGRoom> pgRooms) {
        userDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, HashMap> snapshotValue = (HashMap<String, HashMap>) snapshot.getValue();
                if (snapshotValue != null) {
                    Set<String> keySet = snapshotValue.keySet();

                    for (PGRoom pgRoom : pgRooms) {
                        List<Feedback> feedbackList = pgRoom.getFeedbackList();
                        if (feedbackList != null) {
                            for (Feedback feedback : feedbackList) {
                                for (String key : keySet) {
                                    HashMap map = snapshotValue.get(key);
                                    Gson gson = new Gson();
                                    JsonElement jsonElement = gson.toJsonTree(map);
                                    User user = gson.fromJson(jsonElement, User.class);
                                    user.setUId(key);

                                    if (feedback.getUserId().equals(user.getUId())) {
                                        feedback.setUser(user);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                pgListener.onGetPG(pgRooms);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                        ArrayList<PGRequest> pgRequests = new ArrayList<>();
                        Set<String> keySet = snapshotValue.keySet();
                        for (String key : keySet) {
                            HashMap map = snapshotValue.get(key);
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(map);
                            PGRequest request = gson.fromJson(jsonElement, PGRequest.class);
                            request.setUId(key);
                            pgRequests.add(request);
                        }
                        mapRequestPGRoom(pgRequests);
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

    private void mapRequestPGRoom(ArrayList<PGRequest> pgRequests) {

        pgDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, HashMap> snapshotValue = (HashMap<String, HashMap>) snapshot.getValue();
                if (snapshotValue != null) {
                    Set<String> keySet = snapshotValue.keySet();

                    for (PGRequest request : pgRequests) {
                        for (String key : keySet) {
                            HashMap map = snapshotValue.get(key);
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(map);
                            PGRoom pgRoom = gson.fromJson(jsonElement, PGRoom.class);
                            pgRoom.setUId(key);

                            if (request.getPgUid().equals(pgRoom.getUId())) {
                                request.setPgRoom(pgRoom);
                                break;
                            }
                        }
                    }
                }

                mapRequestUser(pgRequests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getDetails());
            }
        });

    }

    private void mapRequestUser(ArrayList<PGRequest> pgRequests) {
        userDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, HashMap> snapshotValue = (HashMap<String, HashMap>) snapshot.getValue();
                if (snapshotValue != null) {
                    Set<String> keySet = snapshotValue.keySet();

                    for (PGRequest request : pgRequests) {
                        for (String key : keySet) {
                            HashMap map = snapshotValue.get(key);
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(map);
                            User user = gson.fromJson(jsonElement, User.class);

                            if (request.getRequestUserId().equals(user.getUId())) {
                                request.setRequestedUser(user);
                            }

                            if (request.getTargetUserId().equals(user.getUId())) {
                                request.setTargetUser(user);
                            }
                        }
                    }
                }

                requestListener.onGetPGRequest(pgRequests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void acceptRejectRequests(PGRequest request, String status) {
        requestsDbReference.child(request.getUId()).child("status").setValue(status);
    }

    public void getFeedback() {
        feedbackDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (feedbackDbReference != null) {
                    HashMap<String, HashMap> snapshotValue = (HashMap<String, HashMap>) snapshot.getValue();
                    if (snapshotValue != null) {
                        Set<String> keySet = snapshotValue.keySet();
                        ArrayList<Feedback> feedbacks = new ArrayList<>();
                        for (String key : keySet) {
                            HashMap map = snapshotValue.get(key);
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(map);
                            Feedback feedback = gson.fromJson(jsonElement, Feedback.class);
                            feedback.setUId(key);
                            feedbacks.add(feedback);
                        }

                        mapFeedbackUser(feedbacks);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void mapFeedbackUser(ArrayList<Feedback> feedbacks) {
        userDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, HashMap> snapshotValue = (HashMap<String, HashMap>) snapshot.getValue();
                if (snapshotValue != null) {
                    Set<String> keySet = snapshotValue.keySet();

                    for (Feedback feedback : feedbacks) {
                        for (String key : keySet) {
                            HashMap map = snapshotValue.get(key);
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(map);
                            User user = gson.fromJson(jsonElement, User.class);

                            if (feedback.getUserId().equals(user.getUId())) {
                                feedback.setUser(user);
                                break;
                            }
                        }
                    }
                }

                feedbackListener.onFeedback(feedbacks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUsers() {
        userDbReference.orderByChild("Status").equalTo(UserState.ACTIVE.getValue())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, HashMap> snapshotValue = (HashMap<String, HashMap>) snapshot.getValue();
                        if (snapshotValue != null) {
                            Set<String> keySet = snapshotValue.keySet();
                            List<User> userList = new ArrayList<>();
                            for (String key : keySet) {
                                HashMap map = snapshotValue.get(key);
                                Gson gson = new Gson();
                                JsonElement jsonElement = gson.toJsonTree(map);
                                User user = gson.fromJson(jsonElement, User.class);
                                user.setUId(key);
                                userList.add(user);
                            }
                            userListener.onListUser(userList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: " + error.getDetails());
                    }
                });
    }

    public void addPGFeedback(PGRoom pgRoom) {
        pgDbReference.child(pgRoom.getUId()).child("feedbackList").setValue(pgRoom.getFeedbackList());
    }

    public void deleteUser(User user) {
        userDbReference.child(user.getUId()).child("Status").setValue(UserState.DELETED.getValue());
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

        public Builder feedbackListener(FeedbackListener feedbackListener) {
            databaseManager.feedbackListener = feedbackListener;
            return this;
        }

        public DatabaseManager build() {
            return this.databaseManager;
        }
    }
}
