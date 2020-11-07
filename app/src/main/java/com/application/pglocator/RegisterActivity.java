package com.application.pglocator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.application.pglocator.auth.AuthManager;
import com.application.pglocator.model.User;

public class RegisterActivity extends AppCompatActivity {

    public static final String ARG_USER = "user";
    private User user;
    private Button buttonRegisterComplete;
    private Spinner spinnerUserType;
    private EditText editTextPhone;
    private EditText editTextAddress;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = (User) getIntent().getSerializableExtra(ARG_USER);

        init();
        initLayout();
        initListeners();
    }

    private void init() {
        authManager = new AuthManager.Builder()
                .activity(this)
                .build();
    }

    private void initLayout() {
        TextView textViewTitle = findViewById(R.id.textViewRegisterTitle);
        textViewTitle.setText(String.format(getString(R.string.complete_register_title), user.getDisplayName()));

        buttonRegisterComplete = findViewById(R.id.buttonRegisterComplete);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
    }

    private void initListeners() {
        buttonRegisterComplete.setOnClickListener(v -> {
            user.setUserType(spinnerUserType.getSelectedItem().toString());
            user.setPhone(editTextPhone.getText().toString());
            user.setAddress(editTextAddress.getText().toString());

            authManager.createUser(user);
            Toast.makeText(this, "Registration Complete", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}