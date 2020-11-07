package com.application.pglocator.model;

import java.util.Date;

import lombok.Data;

@Data
public class PGRequest {
    private String uId;
    private String requestUserId;
    private String targetUserId;
    private String pgUid;
    private Date requestTime;
    private String status;
}
