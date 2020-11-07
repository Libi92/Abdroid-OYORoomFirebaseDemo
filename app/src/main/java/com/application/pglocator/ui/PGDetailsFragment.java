package com.application.pglocator.ui;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.application.pglocator.R;
import com.application.pglocator.constants.UserType;
import com.application.pglocator.db.DatabaseManager;
import com.application.pglocator.db.UserListener;
import com.application.pglocator.model.PGRequest;
import com.application.pglocator.model.PGRoom;
import com.application.pglocator.model.User;
import com.application.pglocator.util.Globals;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

import pereira.agnaldo.previewimgcol.ImageCollectionView;

public class PGDetailsFragment extends Fragment implements UserListener {
    public static final String ARG_PGROOM = "PG::Room";
    private PGRoom pgRoom;

    private ImageCollectionView imageCollectionView;
    private DatabaseManager databaseManager;
    private View view;
    private Button buttonRequest;

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


        textViewTitle.setText(pgRoom.getTitle());
        textViewDescription.setText(pgRoom.getDescription());
        textViewAddress.setText(pgRoom.getAddress());
        textViewRent.setText(String.format("Rs. %s (per month)", pgRoom.getRent()));

        buttonRequest = view.findViewById(R.id.buttonRequest);

        setImages(pgRoom);
        User user = new User();
        user.setUId(pgRoom.getUserId());
        databaseManager.getUser(user);
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
                        pgRequest.setRequestTime(Calendar.getInstance().getTime());
                        pgRequest.setTargetUserId(pgRoom.getUserId());
                        pgRequest.setStatus("PENDING");
                        databaseManager.createRequest(pgRequest);

                        Toast.makeText(getContext(), "Request sent", Toast.LENGTH_SHORT).show();
                    }))
                    .setNegativeButton("Cancel", ((dialog, which) -> {

                    })).show();
        });

        buttonRequest.setVisibility(View.GONE);
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
        textViewPhone.setText(user.getPhone());

        if (Globals.user.getUserType().equals(UserType.USER.getValue())) {
            buttonRequest.setVisibility(View.VISIBLE);
        }
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
