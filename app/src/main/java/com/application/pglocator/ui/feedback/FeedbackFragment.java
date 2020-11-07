package com.application.pglocator.ui.feedback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pglocator.R;
import com.application.pglocator.adapter.FeedbackAdapter;
import com.application.pglocator.db.DatabaseManager;
import com.application.pglocator.db.FeedbackListener;
import com.application.pglocator.model.Feedback;

import java.util.List;

public class FeedbackFragment extends Fragment implements FeedbackListener {

    private RecyclerView recyclerViewFeedbacks;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        initLayout(view);
        init();
        return view;
    }

    private void init() {
        DatabaseManager databaseManager = new DatabaseManager.Builder()
                .feedbackListener(this)
                .build();

        databaseManager.getFeedback();
    }

    private void initLayout(View view) {
        recyclerViewFeedbacks = view.findViewById(R.id.recyclerViewFeedbacks);
        recyclerViewFeedbacks.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void onFeedback(List<Feedback> feedbacks) {
        FeedbackAdapter adapter = new FeedbackAdapter(feedbacks);
        recyclerViewFeedbacks.setAdapter(adapter);
    }
}