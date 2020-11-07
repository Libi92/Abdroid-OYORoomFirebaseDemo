package com.application.pglocator.db;

import com.application.pglocator.model.Feedback;

import java.util.List;

public interface FeedbackListener {
    void onFeedback(List<Feedback> feedbacks);
}
