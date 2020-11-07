package com.application.pglocator.model;

import lombok.Data;

@Data
public class Feedback {
    private String uId;
    private String userId;
    private String title;
    private String description;
    private Long feedbackTime;

    private User user;
}
