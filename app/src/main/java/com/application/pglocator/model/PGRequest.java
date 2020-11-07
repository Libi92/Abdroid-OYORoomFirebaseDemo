package com.application.pglocator.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class PGRequest implements Serializable {
    private String uId;
    private String requestUserId;
    private String targetUserId;
    private String pgUid;
    private Long requestTime;
    private String status;

    private User requestedUser;
    private User targetUser;
    private PGRoom pgRoom;
}
