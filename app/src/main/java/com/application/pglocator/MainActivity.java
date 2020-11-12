package com.application.pglocator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.pglocator.adapter.PGAdapter;
import com.application.pglocator.auth.AuthListener;
import com.application.pglocator.auth.AuthManager;
import com.application.pglocator.constants.UserState;
import com.application.pglocator.db.DatabaseManager;
import com.application.pglocator.db.PGListener;
import com.application.pglocator.db.UserListener;
import com.application.pglocator.model.PGRoom;
import com.application.pglocator.model.User;
import com.application.pglocator.util.Globals;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AuthListener, UserListener, PGListener, PGAdapter.PGClickListener {

    private static final int RC_SIGN_IN = 1009;
    private static final String TAG = MainActivity.class.getSimpleName();

    private Menu menu;
    private User userModel;
    private AuthManager authManager;
    private DatabaseManager databaseManager;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerViewPG;
    private boolean registerChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initLayout();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkAuth();
    }

    private void init() {
        databaseManager = new DatabaseManager.Builder()
                .userListener(this)
                .pgListener(this)
                .build();
        authManager = new AuthManager.Builder()
                .activity(this)
                .loginReqCode(RC_SIGN_IN)
                .authListener(this)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    private void checkAuth() {
        if (isLoggedIn()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            userModel = new User(currentUser);
            databaseManager.getUser(userModel);
        }
    }

    private boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void initLayout() {
        floatingActionButton = findViewById(R.id.floatingActionButton);
        recyclerViewPG = findViewById(R.id.recyclerViewPG);
        recyclerViewPG.setLayoutManager(new LinearLayoutManager(this));

        databaseManager.getPG(null);
    }

    private void initListeners() {
        floatingActionButton.setOnClickListener(v -> {
            if (userModel != null) {
                Intent intent = new Intent(this, PGHomeActivity.class);
                intent.putExtra(PGHomeActivity.ARG_USER, userModel);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authManager.handleLoginResult(requestCode, resultCode, data);
    }

    private void showSnackbar(String msg) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        this.menu = menu;

        toggleLogin(!isLoggedIn());
        showRegisterMenu(false);

        return super.onCreateOptionsMenu(menu);
    }

    private void toggleLogin(boolean showLogin) {
        if (this.menu != null) {
            this.menu.findItem(R.id.menu_login).setVisible(showLogin);
            this.menu.findItem(R.id.menu_logout).setVisible(!showLogin);
        }
    }

    private void showRegisterMenu(boolean showRegister) {
        if (this.menu != null) {
            this.menu.findItem(R.id.menu_register).setVisible(showRegister);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_login) {
            authManager.doLogin();
        } else {
            authManager.doLogout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoginSuccess(User user) {
        toggleLogin(false);
    }

    @Override
    public void onLoginFailed() {
        showSnackbar("Login Failed");
    }

    @Override
    public void onLogout() {
        toggleLogin(true);
        showRegisterMenu(false);
        showSnackbar("Logged out");
        floatingActionButton.setVisibility(View.GONE);
    }

    @Override
    public void registerUser(User user) {
        showSnackbar("Registration pending");

        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra(RegisterActivity.ARG_USER, user);
        startActivity(intent);
    }

    @Override
    public void onGetUser(User user) {
        Globals.user = user;
        if (!user.isRegistered()) {
            if (!registerChecked) {
                registerChecked = true;
                registerUser(user);
            }
            showRegisterMenu(true);
        } else {
            String status = user.getStatus();
            if (status != null && status.equals(UserState.DELETED.getValue())) {
                showSnackbar("User account deleted");
                authManager.doLogout();
                Globals.user = null;
                return;
            }
            showRegisterMenu(false);
            showSnackbar(String.format("Welcome %s", user.getDisplayName()));
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onListUser(List<User> users) {

    }

    @Override
    public void onGetPG(List<PGRoom> rooms) {
        PGAdapter pgAdapter = new PGAdapter(rooms);
        recyclerViewPG.setAdapter(pgAdapter);
    }

    @Override
    public void onPGItemClick(PGRoom pgRoom) {

    }
}