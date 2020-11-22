package com.application.pglocator.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.application.pglocator.model.PGRoom;
import com.application.pglocator.util.Globals;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import pereira.agnaldo.previewimgcol.ImageCollectionView;

public class AddPGBottomFragment extends BottomSheetDialogFragment {
    private static final String TAG = AddPGBottomFragment.class.getSimpleName();
    private Button buttonAddPhoto;
    private ImageCollectionView imageCollectionView;
    private Button buttonAddPG;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextAddress;
    private EditText editTextMaxPeople;
    private EditText editTextRent;
    private EditText editTextLocation;

    private List<Image> images;

    private DatabaseManager databaseManager;
    private StorageReference storageRef;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_pgbottom, container, false);

        init();
        initLayout(view);
        initListeners();

        return view;
    }

    private void init() {
        databaseManager = new DatabaseManager.Builder().build();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initLayout(View view) {
        buttonAddPhoto = view.findViewById(R.id.buttonAddPhoto);
        imageCollectionView = view.findViewById(R.id.imageCollectionView);

        buttonAddPG = view.findViewById(R.id.buttonAddPG);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextAddress = view.findViewById(R.id.editTextAddress);
        editTextMaxPeople = view.findViewById(R.id.editTextMaxPeople);
        editTextRent = view.findViewById(R.id.editTextRent);
        editTextLocation = view.findViewById(R.id.editTextLocation);
    }

    private void initListeners() {
        buttonAddPhoto.setOnClickListener(v -> {
            ImagePicker.create(this)
                    .limit(5)
                    .start();
        });

        buttonAddPG.setOnClickListener(v -> {
            PGRoom pgRoom = getPgRoom();
            pgRoom.setImages(new ArrayList<>());

            initProgressDialog();

            AtomicInteger counter = new AtomicInteger();
            if (images != null && !images.isEmpty()) {

                for (Image image : images) {
                    Uri file = Uri.fromFile(new File(image.getPath()));
                    StorageReference imageRef = storageRef.child("images/" + file.getLastPathSegment());
                    UploadTask uploadTask = imageRef.putFile(file);


                    uploadTask.addOnFailureListener(exception -> {
                        Log.e(TAG, "initListeners: image upload failed");
                        handleSave(counter, pgRoom);
                    }).addOnCompleteListener(task -> {
                        Task<Uri> downloadUrl = imageRef.getDownloadUrl();
                        downloadUrl.addOnCompleteListener(task1 -> {
                            Uri uri = task1.getResult();
                            pgRoom.getImages().add(uri.toString());
                            Log.i(TAG, "initListeners: adding Uri - " + uri);

                            handleSave(counter, pgRoom);
                        });
                    });
                }
            }
        });
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading image, please wait.");
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    private void handleSave(AtomicInteger counter, PGRoom pgRoom) {
        counter.addAndGet(1);
        progressDialog.setProgress(counter.intValue() / images.size() * 100);
        if (counter.intValue() == images.size()) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();

                pgRoom.setUserId(Globals.user.getUId());
                databaseManager.createPG(pgRoom);

                Toast.makeText(getContext(), "PG created", Toast.LENGTH_SHORT).show();
                dismiss();

            }
        }
    }

    @NotNull
    private PGRoom getPgRoom() {
        PGRoom pgRoom = new PGRoom();
        pgRoom.setTitle(editTextTitle.getText().toString());
        pgRoom.setDescription(editTextDescription.getText().toString());
        pgRoom.setLocation(editTextLocation.getText().toString());
        pgRoom.setAddress(editTextAddress.getText().toString());
        pgRoom.setMaxPeople(Integer.parseInt(editTextMaxPeople.getText().toString()));
        pgRoom.setRent(Float.parseFloat(editTextRent.getText().toString()));
        return pgRoom;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            images = ImagePicker.getImages(data);

            ArrayList<Bitmap> bitmaps = new ArrayList<>();
            for (Image image : images) {
                Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
                bitmaps.add(bitmap);
            }
            imageCollectionView.addImages(bitmaps);
            Log.i(TAG, "onActivityResult: " + images);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
