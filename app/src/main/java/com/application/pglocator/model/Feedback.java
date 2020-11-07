package com.application.pglocator.model;

import java.util.Date;

import lombok.Data;

@Data
public class Feedback {
    private String uId;
    private String userId;
    private String title;
    private String description;
    private Date feedbackTime;
}
