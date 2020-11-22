package com.application.pglocator.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class PGRoom implements Serializable {
    private String uId;
    private String userId;
    private String title;
    private String description;
    private String location;
    private String address;
    private int maxPeople;
    private float rent;
    private List<String> images;
    private List<Feedback> feedbackList;
}
