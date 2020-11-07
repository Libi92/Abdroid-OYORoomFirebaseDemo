package com.application.pglocator.ui.feedback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.application.pglocator.R;
import com.application.pglocator.db.DatabaseManager;
import com.application.pglocator.model.Feedback;
import com.application.pglocator.util.Globals;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

public class AddFeedbackFragment extends BottomSheetDialogFragment {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Button buttonAddFeedback;
    private DatabaseManager databaseManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_add, container, false);

        init();
        initLayout(view);
        initListeners();

        return view;
    }

    private void init() {
        databaseManager = new DatabaseManager.Builder()
                .build();
    }

    private void initLayout(View view) {
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonAddFeedback = view.findViewById(R.id.buttonAddFeedback);
    }

    private void initListeners() {
        buttonAddFeedback.setOnClickListener(v -> {
            Feedback feedback = new Feedback();
            feedback.setTitle(editTextTitle.getText().toString());
            feedback.setDescription(editTextDescription.getText().toString());
            feedback.setFeedbackTime(Calendar.getInstance().getTime());
            feedback.setUserId(Globals.user.getUId());

            databaseManager.createFeedback(feedback);
            Toast.makeText(getContext(), "Feedback Added", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}
