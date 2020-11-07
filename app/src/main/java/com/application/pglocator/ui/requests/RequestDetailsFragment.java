package com.application.pglocator.ui.requests;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.application.pglocator.R;
import com.application.pglocator.constants.RequestAction;
import com.application.pglocator.constants.UserType;
import com.application.pglocator.db.DatabaseManager;
import com.application.pglocator.model.PGRequest;
import com.application.pglocator.model.PGRoom;
import com.application.pglocator.model.User;
import com.application.pglocator.util.DateUtil;
import com.application.pglocator.util.Globals;

import java.io.IOException;
import java.net.URL;

import pereira.agnaldo.previewimgcol.ImageCollectionView;

public class RequestDetailsFragment extends Fragment {

    public static final String ARG_REQUEST = "arg::Request";
    private PGRequest request;
    private DatabaseManager databaseManager;

    private ImageCollectionView imageCollectionView;
    private Button buttonAccept;
    private Button buttonReject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_details, container, false);

        init();

        initLayout(view);
        initListeners();
        return view;
    }

    private void init() {
        databaseManager = new DatabaseManager.Builder()
                .build();
        Bundle bundle = getArguments();
        if (bundle != null) {
            request = (PGRequest) bundle.getSerializable(ARG_REQUEST);
        }
    }

    private void initLayout(View view) {
        imageCollectionView = view.findViewById(R.id.imageCollectionView);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        TextView textViewAddress = view.findViewById(R.id.textViewAddress);
        TextView textViewRent = view.findViewById(R.id.textViewRent);

        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewPhone = view.findViewById(R.id.textViewPhone);
        TextView textViewRequestOn = view.findViewById(R.id.textViewRequestedOn);

        TextView textViewStatus = view.findViewById(R.id.textViewStatus);


        if (request == null) return;

        PGRoom pgRoom = request.getPgRoom();
        if (pgRoom != null) {
            textViewTitle.setText(pgRoom.getTitle());
            textViewDescription.setText(pgRoom.getDescription());
            textViewAddress.setText(pgRoom.getAddress());
            textViewRent.setText(String.format("Rs. %s (per month)", pgRoom.getRent()));

            User user;
            if (Globals.user.getUserType().equals(UserType.USER.getValue())) {
                user = request.getTargetUser();
            } else {
                user = request.getRequestedUser();
            }

            textViewName.setText(user.getDisplayName());
            textViewPhone.setText(user.getPhone());
            textViewRequestOn.setText(String.format("Requested On: %s", DateUtil.getDate(request.getRequestTime())));

            buttonAccept = view.findViewById(R.id.buttonAccept);
            buttonReject = view.findViewById(R.id.buttonReject);

            if (!request.getStatus().equals(RequestAction.Pending.getValue())) {
                buttonAccept.setVisibility(View.GONE);
                buttonReject.setVisibility(View.GONE);
                textViewStatus.setText(request.getStatus());
            } else {
                textViewStatus.setVisibility(View.GONE);
            }

            setImages(pgRoom);
        }
    }

    private void initListeners() {
        buttonAccept.setOnClickListener(v -> {
            databaseManager.acceptRejectRequests(request, RequestAction.Accept.getValue());
            completeAction("Request Accepted");
        });
        buttonReject.setOnClickListener(v -> {
            databaseManager.acceptRejectRequests(request, RequestAction.Reject.getValue());
            completeAction("Request Rejected");
        });
    }

    private void completeAction(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.popBackStack();
    }

    private void setImages(PGRoom pgRoom) {
        for (String imgUrl : pgRoom.getImages()) {
            new ImageLoadTask().execute(imgUrl);
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
