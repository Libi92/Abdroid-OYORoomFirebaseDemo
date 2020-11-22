package com.application.pglocator.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pglocator.R;
import com.application.pglocator.adapter.FeedbackAdapter;
import com.application.pglocator.constants.RequestAction;
import com.application.pglocator.constants.UserType;
import com.application.pglocator.db.DatabaseManager;
import com.application.pglocator.db.UserListener;
import com.application.pglocator.model.Feedback;
import com.application.pglocator.model.PGRequest;
import com.application.pglocator.model.PGRoom;
import com.application.pglocator.model.User;
import com.application.pglocator.util.Globals;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pereira.agnaldo.previewimgcol.ImageCollectionView;

public class PGDetailsFragment extends Fragment implements UserListener {
    public static final String ARG_PGROOM = "PG::Room";
    private static final int REQUEST_CODE = 1992;
    private PGRoom pgRoom;

    private ImageCollectionView imageCollectionView;
    private DatabaseManager databaseManager;
    private View view;
    private Button buttonRequest;
    private Button buttonSendFeedback;
    private RecyclerView recyclerViewFeebacks;
    private EditText editTextPGFeedback;
    private List<Feedback> pgFeedbacks;
    private FeedbackAdapter feedbackAdapter;
    private String phone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pg_details, container, false);

        init();
        getPGData();
        initListeners();

        return view;
    }

    private void init() {
        databaseManager = new DatabaseManager.Builder()
                .userListener(this)
                .build();
    }

    private void getPGData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            pgRoom = (PGRoom) bundle.getSerializable(ARG_PGROOM);
            initLayout();
        }
    }

    private void initLayout() {
        imageCollectionView = view.findViewById(R.id.imageCollectionView);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        TextView textViewAddress = view.findViewById(R.id.textViewAddress);
        TextView textViewRent = view.findViewById(R.id.textViewRent);
        TextView textViewLocation = view.findViewById(R.id.textViewLocation);
        TextView textViewMaxAllowed = view.findViewById(R.id.textViewMaxAllowed);


        textViewTitle.setText(pgRoom.getTitle());
        textViewDescription.setText(pgRoom.getDescription());
        textViewAddress.setText(pgRoom.getAddress());
        textViewRent.setText(String.format("Rs. %s (per month)", pgRoom.getRent()));
        textViewLocation.setText(pgRoom.getLocation());
        textViewMaxAllowed.setText(String.valueOf(pgRoom.getMaxPeople()));

        buttonRequest = view.findViewById(R.id.buttonRequest);

        setImages(pgRoom);

        editTextPGFeedback = view.findViewById(R.id.editTextPGFeedback);
        buttonSendFeedback = view.findViewById(R.id.buttonSend);
        recyclerViewFeebacks = view.findViewById(R.id.recyclerViewPGFeedback);
        recyclerViewFeebacks.setLayoutManager(new LinearLayoutManager(requireContext()));

        if (Globals.user.getUserType().equals(UserType.PG.getValue())) {
            editTextPGFeedback.setVisibility(View.GONE);
            buttonSendFeedback.setVisibility(View.GONE);
        }
        setFeedback();

        User user = new User();
        user.setUId(pgRoom.getUserId());
        databaseManager.getUser(user);
    }

    private void setFeedback() {
        pgFeedbacks = pgRoom.getFeedbackList();
        if (pgFeedbacks == null) {
            pgFeedbacks = new ArrayList<>();
        }

        feedbackAdapter = new FeedbackAdapter(pgFeedbacks);
        recyclerViewFeebacks.setAdapter(feedbackAdapter);
    }

    private void initListeners() {
        buttonRequest.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirm")
                    .setMessage("Please confirm requesting PG")
                    .setPositiveButton("Confirm", ((dialog, which) -> {
                        PGRequest pgRequest = new PGRequest();
                        pgRequest.setPgUid(pgRoom.getUId());
                        pgRequest.setRequestUserId(Globals.user.getUId());
                        pgRequest.setRequestTime(Calendar.getInstance().getTime().getTime());
                        pgRequest.setTargetUserId(pgRoom.getUserId());
                        pgRequest.setStatus(RequestAction.Pending.getValue());
                        databaseManager.createRequest(pgRequest);

                        Toast.makeText(getContext(), "Request sent", Toast.LENGTH_SHORT).show();
                    }))
                    .setNegativeButton("Cancel", ((dialog, which) -> {

                    })).show();
        });

        buttonRequest.setVisibility(View.GONE);

        buttonSendFeedback.setOnClickListener(v -> {
            String feedback = editTextPGFeedback.getText().toString();
            Feedback pgFeedback = new Feedback();
            pgFeedback.setDescription(feedback);
            pgFeedback.setFeedbackTime(Calendar.getInstance().getTime().getTime());
            pgFeedback.setUserId(Globals.user.getUId());

            if (pgRoom.getFeedbackList() == null) {

                pgRoom.setFeedbackList(new ArrayList<>());
            }
            pgRoom.getFeedbackList().add(pgFeedback);

            databaseManager.addPGFeedback(pgRoom);

            pgFeedbacks.add(pgFeedback);
            feedbackAdapter.notifyDataSetChanged();

            editTextPGFeedback.setText("");
            Toast.makeText(requireContext(), "Feedback added", Toast.LENGTH_SHORT).show();
        });
    }

    private void setImages(PGRoom pgRoom) {

        for (String imgUrl : pgRoom.getImages()) {

            new ImageLoadTask().execute(imgUrl);
        }
    }

    @Override
    public void onGetUser(User user) {
        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewPhone = view.findViewById(R.id.textViewPhone);

        textViewName.setText(user.getDisplayName());
        String phone = user.getPhone();

        if (phone == null || phone.equals("")) {
            textViewPhone.setText("not available");
        } else {
            textViewPhone.setText(phone);

            textViewPhone.setOnClickListener(v ->
                    checkCallPermission(phone)
            );

        }


        if (Globals.user.getUserType().equals(UserType.USER.getValue())) {
            buttonRequest.setVisibility(View.VISIBLE);
        }
    }

    private void checkCallPermission(String phone) {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {

            makeCall(phone);
        } else {
            this.phone = phone;
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE);
        }

    }

    private void makeCall(String phone) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Call this PG")
                .setMessage("Carrier charges will be applicable")
                .setPositiveButton("Ok", (dialog, which) -> {
                    String uri = "tel:" + phone;
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall(this.phone);
            } else {
                Snackbar.make(view, "Permission is required to make call",
                        Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onListUser(List<User> users) {

    }

    class ImageLoadTask extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... imgUrl) {
            try {
                URL url = new URL(imgUrl[0]);
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageCollectionView.addImage(bitmap);
            }
            super.onPostExecute(bitmap);
        }
    }
}
